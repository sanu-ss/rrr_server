package com.rrrs.tableconfig.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.tableconfig.entities.ConfigInfo;
import com.rrrs.tableconfig.entities.MapInfo;
import com.rrrs.user.entity.RrrCommonDtls;
import com.rrrs.util.ConfigUtils;
import com.rrrs.util.ReadPropertyFile;
@Repository
public class JdbcConfigDaoImp implements JdbcConfigDao{

	private Logger logger=LoggerFactory.getLogger(JdbcConfigDaoImp.class);
	private EntityManager entityManager;
	public JdbcConfigDaoImp(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("tableconfig/tableconfig.properties");
	@Override
	public boolean checkTable(String tablename) {
		this.logger.info("Checking table name before creating for given formats in Dao class");
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(readPropertyFile.getProperty("CHECK_TAB_DETAILS"));
		query.setParameter("tablename", tablename.toUpperCase());
		Long count =(Long) query.uniqueResult();
		return (count==0)?false:true;
	}

	@Override
	public boolean checkFormat(String formatname) {
		this.logger.info("checking format name before creating");
		Session session=this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CHECK_FORMAT_DETAILS"));
		query.setParameter("formatname", formatname);
		Long count = (Long)query.uniqueResult();
		return (count==0)?false:true;
	}

	@Override
	public boolean persistConfigAndMapInfoDtls(ConfigInfo format) {
		this.logger.info("inserting data of config and map info into table");
		Session session = this.entityManager.unwrap(Session.class);
		Transaction tx = session.beginTransaction();
		boolean status=false;
		try {
			session.persist(format);
			this.logger.info("successfully inserted data in table");
			createConfigTbl(format,session);
			status=true;
			tx.commit();
		} catch (Exception e) {
			this.logger.info("error while saving data of config details and mapping details in table "+e);
			this.logger.info("rollback the data");
			tx.rollback();
			status=false;
		}
		session.close();
		return status;
	}
	public boolean createConfigTbl(ConfigInfo format,Session session) {
		this.logger.info("JdbcConfigDaoImpl >>> Inside createConfig Tbl method ");
		Map<String,String> queries= ConfigUtils.createQry(format,format.getRrrCommonDtls().getCompanyId());
		boolean status;
		logger.info(" Dynamic table Db script >> "+queries.get("createScript"));
		logger.info("duplicate query="+queries.get("duplicateScript"));
		//Session session = this.entityManager.unwrap(Session.class);
		//Transaction tx = session.beginTransaction();
		try {
			logger.info("creating config table");
			session.createNativeQuery(queries.get("createScript")).executeUpdate();
			session.createNativeQuery(queries.get("duplicateScript")).executeUpdate();
			logger.info("Exit createConfigTbl method ");
			status=true;
			//tx.commit();
		}catch(Exception e) {
			logger.info("error while creating the original table and shadow table"+e);
			status=false;
		}
		return status;
	}

	@Override
	public ConfigInfo setRequidFieldData(ConfigInfo format) {
		Session session = this.entityManager.unwrap(Session.class);
		Query<RrrCommonDtls> query = session.createQuery(this.readPropertyFile.getProperty("GET_COMPANY_ID"));
		query.setParameter("username", format.getRrrCommonDtls().getUserIns());
		RrrCommonDtls commonData = query.uniqueResult();
		System.out.println("common data"+commonData);

		format.getRrrCommonDtls().setCompanyId(commonData.getCompanyId());
		for(MapInfo map:format.getMappings()) {
			//
			RrrCommonDtls data=new RrrCommonDtls();
			data.setCompanyId(commonData.getCompanyId());
			data.setUserIns(commonData.getUserIns());
			data.setDateIns(format.getRrrCommonDtls().getDateIns());
			map.setRrrCommonDtls(data);
		}
		format.setTablename(format.getTablename().toUpperCase());
		format.setFormatname(format.getFormatname().toUpperCase());
		format.setOrgTabName(format.getTablename()+"_"+format.getRrrCommonDtls().getCompanyId());
		if(format.getDelimiter()==null)
			format.setDelimiter("");
		format.setShadowTabName(format.getTablename()+"_SH"+"_"+format.getRrrCommonDtls().getCompanyId());
		System.out.println("all data set");
		return format;
	}

	@Override
	public List<ConfigInfo> getFormatList(String username) {
		Session session = this.entityManager.unwrap(Session.class);
		Query company = session.createQuery("select user.rrrCommonDtls.companyId from CurrentUser user where user.username=:username");
		company.setParameter("username", username);
		Integer companyId = (Integer)company.uniqueResult();
        System.out.println("companyId--<>"+companyId);
		//Query<ConfigInfo> query = session.createQuery("from ConfigInfo configInfo where configInfo.rrrCommonDtls.companyId=:companyId");
        Query query = session.createQuery(this.readPropertyFile.getProperty("GET_FORMAT_DTLS"));
        query.setParameter("companyId",companyId);
		List<ConfigInfo> list = new ArrayList<>();
        List <Object[]>configList = query.getResultList();
		System.out.println(configList.get(0)[configList.get(0).length-1]);
		for(Object[] obj:configList) {
			ConfigInfo config=new ConfigInfo();
			config.setConfigId((int)obj[0]);
			config.setFormatname((String)obj[2]);
			config.setTablename((String)obj[1]);
			config.setDesc((String)obj[3]);
			config.setFileExt((String)obj[4]);
			config.setColCount((int)obj[5]);
			config.setDelimiter((String)obj[6]);
			config.setRrrCommonDtls((RrrCommonDtls)obj[7]);
			if((Long)obj[8]>0) {
				config.setFlag(false);
			}else {
				config.setFlag(true);
			}
			list.add(config);
		}
		System.out.println("data-->"+list);
		return list;
	}

	@Override
	public List<MapInfo> getFormatMappingData(int formatId, int companyId) {
		Session session = this.entityManager.unwrap(Session.class);
		Query <MapInfo> query = session.createQuery(this.readPropertyFile.getProperty("GET_FORMAT_MAPPING_DTLS"));
		query.setParameter("configId", formatId);
		query.setParameter("companyId", companyId);
		List<MapInfo> list = query.list();
		return list;
	}

	@Override
	public String getFile(int configId, String formatTableStructureDirectory, Map<String, List<String>> colAndDatatype,
			String fileName,String username) {

		List<String> columns = colAndDatatype.get("column");
		Workbook wb = new SXSSFWorkbook();
		String directoryBuilder =  new StringBuilder(formatTableStructureDirectory).append( File.separator)
				.append(fileName+"_"+username).append(ConfigUtils.EXCEL_FILE_EXTENSION).toString();

		Session session = this.entityManager.unwrap(Session.class);
		Query<MapInfo> query = session.createQuery("select config.mappings from ConfigInfo config where config.configId=:configId");
		query.setParameter("configId", configId);
		List<MapInfo> data = query.list();
		System.out.println("data------------->"+data);
		int sheetNum = 1;
		int rowNumber=0;
		int reCount=0;
		SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("sheet"+"-"+sheetNum);	
		for(MapInfo map:data) {

			if(rowNumber==0){
				Row row = sheet.createRow(rowNumber++);
				int cellnum=0;
				for(int i=0;i<columns.size();i++){
					Cell cell = row.createCell(cellnum++);
					if(columns.get(i) != null && columns.get(i) !="")
						cell.setCellValue(columns.get(i).toString());
				}
			}


			Row row = sheet.createRow(rowNumber++);
			int cellnum=0;
			for(int len=0;len<columns.size();len++) {
				Cell cell = row.createCell(cellnum++);
				if(map.getCol() != null && map.getCol() !="" && len==0)
					cell.setCellValue(map.getCol().toString());
				if(map.getTabcol() != null && map.getTabcol() !="" && len==1)
					cell.setCellValue(map.getTabcol().toString());
				if(map.getDatatype() != null && map.getDatatype() !="" && len==2)
					cell.setCellValue(map.getDatatype().toString());
				if(len==3)
					cell.setCellValue(map.getDatasize()+"");
				if(map.getDefaultvalue() != null && map.getDefaultvalue() !="" && len==4)
					cell.setCellValue(map.getDefaultvalue().toString());
			}
			if(reCount==1000000){
				sheetNum++;
				sheet = (SXSSFSheet) wb.createSheet("sheet"+"-"+sheetNum);
				rowNumber=0;
			}
			reCount++;
		}

		boolean storedFile = ConfigUtils.getDownloadFile(directoryBuilder,wb);

		return (storedFile == false) ? String.valueOf(storedFile)  : directoryBuilder;
	}

	@Override
	public boolean getFormatUse(int configId) {
	
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("GET_COUNT_CONFIGID"));
		query.setParameter("configId", configId);
		Long count=(Long)query.uniqueResult();
		return (count==0)?false:true;
	}
	@Override
	public boolean checkUpdateFormat(String formatname,int configId) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CHECK_UPDATE_FORMAT"));
		query.setParameter("formatname", formatname);
		query.setParameter("configId", configId);
		Long count =(Long)query.uniqueResult();
		return (count==0)?false:true;
	}

	@Override
	public boolean checkUpdateTable(String tablename, int configId) {

		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CHECK_UPDATE_TABLE"));
		query.setParameter("tablename", tablename);
		query.setParameter("configId", configId);
		Long count =(Long)query.uniqueResult();
		return (count==0)?false:true;
	}



	@Override
	public boolean updateFormats(ConfigInfo format) {
		
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("GET_ORIGINAL_SHADOW_TABLE"));
		query.setParameter("configId", format.getConfigId());
		//here result data is of type Object(Object[])
		Object data = query.uniqueResult();
		System.out.println("result data-->"+(String)((Object[])data)[0]);
		String orgTab=(String)((Object[])data)[0];
		String shadowTab=(String)((Object[])data)[1];
		
		Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(format);
			//session.update(shadowTab, tx);
			dropOldCreateTable(orgTab,shadowTab,session);
			createConfigTbl(format,session);
			tx.commit();
			return true;
		} catch (Exception e) {
			logger.info("Error while updating the Format");
			return false;
		}
		
	}

	public void dropOldCreateTable(String oldOrgTab,String oldShadowTab,Session session) {
		String dropOrgTab="DROP TABLE "+oldOrgTab;
		String dropOldShadowTab="DROP TABLE "+oldShadowTab;
		Query dropOrgTabDb = session.createNativeQuery(dropOrgTab);
		Query dropOldShadowTabDb = session.createNativeQuery(dropOldShadowTab);
		try {
			dropOrgTabDb.executeUpdate();
			dropOldShadowTabDb.executeUpdate();
			
		} catch (Exception e) {
			logger.info("Error while droping Old format tables");
		}
		
	}

	@Override
	public boolean deleteFormat(Integer formatId) {
	
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("GET_ORIGINAL_SHADOW_TABLE"));
		query.setParameter("configId", formatId);
		//here result data is of type Object(Object[])
		Object data = query.uniqueResult();
		String orgTab=(String)((Object[])data)[0];
		String shadowTab=(String)((Object[])data)[1];
		ConfigInfo configInfo=new ConfigInfo();
		configInfo.setConfigId(formatId);
		Transaction tx = session.beginTransaction();
		try {
			NativeQuery mapdataquery = session.createNativeQuery(this.readPropertyFile.getProperty("DELETE_MAPP_DATA"));
			mapdataquery.setParameter("configId", formatId);
			mapdataquery.executeUpdate();
			Query formatQuery = session.createQuery(this.readPropertyFile.getProperty("DELETE_FORMAT_DATA"));
			formatQuery.setParameter("configId", formatId);
			formatQuery.executeUpdate();
			dropOldCreateTable(orgTab,shadowTab,session);
			tx.commit();
			return true;
		} catch (Exception e) {
			logger.info("Error while updating the Format"+e);
			tx.rollback();
			return false;
		}
	}

		
	
}
