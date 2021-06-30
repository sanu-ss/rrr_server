package com.rrrs.processmapping.dao;

import java.security.Principal;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rrrs.mappingconfig.dao.MappingConfigDaoImp;
import com.rrrs.mappingconfig.entity.MappingDetails;
import com.rrrs.util.CurrentUserDbDtls;
import com.rrrs.util.ReadPropertyFile;
@Repository
public class ProcessMappingDaoImp implements ProcessMappingDao{

	private EntityManager entityManager;
	private CurrentUserDbDtls currentUserDbDtls;
	private ReadPropertyFile readPropertyFile=new ReadPropertyFile("mappingProcess/process.properties");
	private Logger logger=LoggerFactory.getLogger(MappingConfigDaoImp.class);
	public ProcessMappingDaoImp(EntityManager entityManager,CurrentUserDbDtls currentUserDbDtls) {
		this.currentUserDbDtls=currentUserDbDtls;
		this.entityManager=entityManager;
	}
	
	@Override
	public List<MappingDetails> getMappingList(Principal principal) {
		int companyId = this.currentUserDbDtls.getCompanyId(principal.getName());
		Session session = this.entityManager.unwrap(Session.class);
		Query<MappingDetails> query = session.createQuery(this.readPropertyFile.getProperty("MAPPING_LIST_DTLS"));
		query.setParameter("companyId", companyId);
		List<MappingDetails> list = query.list();
		System.out.println("list of mappinng-->"+list);
		return list;
	}

	@Override
	public String getCoreTableName(Integer tableId) {
		Session session = this.entityManager.unwrap(Session.class);
		Query query = session.createQuery(this.readPropertyFile.getProperty("CORE_TABLE_NAME"));
		query.setParameter("tableId", tableId);
		String data = (String)query.uniqueResult();
		System.out.println("data-->"+data);
		return data;
	}

}
