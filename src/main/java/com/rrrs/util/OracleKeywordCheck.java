package com.rrrs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class OracleKeywordCheck {

	private static ReadPropertyFile dataBase=new ReadPropertyFile("application.properties");

	private static Logger LOGGER = LoggerFactory.getLogger(OracleKeywordCheck.class);
	public static enum OracleKeyWord {
		MODIFY, START, EXCLUSIVE, NOAUDIT, SESSION, FILE, NOTFOUND, SHARE, NOWAIT, SQLBUF, SUCCESSFUL, AUDIT, OFFLINE, SYNONYM, SYSDATE, IMMEDIATE, ONLINE, CHAR, IN, THEN, INCREMENT, TO, TRIGGER, COLUMN, INITIAL, UID, COMMENT, PRIVILEGES, CONNECT, RAW, USER, VALIDATE, LOCK, ROW, LONG, MAXEXTENTS, WHENEVER, ROWS, ADMIN, FOUND, MOUNT, AFTER, CYCLE, NEXT, ALLOCATE, GO, ANALYZE, DATAFILE, NOARCHIVELOG, ARCHIVE, DBA, GROUPS, NOCACHE, ARCHIVELOG, DEC, INCLUDING, NOCYCLE, AUTHORIZATION, DECLARE, INDICATOR, NOMAXVALUE, AVG, DISABLE, INITRANS, NOMINVALUE, BACKUP, DISMOUNT, INSTANCE, NONE, BEGIN, DOUBLE, INT, NOORDER, BECOME, DUMP, KEY, NORESETLOGS, BEFORE, EACH, LANGUAGE, NORMAL, BLOCK, ENABLE, LAYER, NOSORT, BODY, END, LINK, NUMERIC, CACHE, ESCAPE, LISTS, OFF, CANCEL, EVENTS, LOGFILE, OLD, CASCADE, EXCEPT, MANAGE, ONLY, CHANGE, EXCEPTIONS, MANUAL, OPEN, CHARACTER, EXEC, MAX, OPTIMAL, CHECKPOINT, EXPLAIN, MAXDATAFILES, OWN, CLOSE, EXECUTE, MAXINSTANCES, PACKAGE, COBOL, EXTENT, MAXLOGFILES, PARALLEL, COMMIT, EXTERNALLY, MAXLOGHISTORY, PCTINCREASE, COMPILE, FETCH, MAXLOGMEMBERS, PCTUSED, CONSTRAINT, FLUSH, MAXTRANS, PLAN, CONSTRAINTS, FREELIST, MAXVALUE, PLI, CONTENTS, FREELISTS, MIN, PRECISION, CONTINUE, FORCE, MINEXTENTS, PRIMARY, CONTROLFILE, FOREIGN, MINVALUE, PRIVATE, COUNT, FORTRAN, MODULE, PROCEDURE, PROFILE, SAVEPOINT, SQLSTATE, TRACING, QUOTA, SCHEMA, STATEMENT_ID, TRANSACTION, READ, SCN, STATISTICS, TRIGGERS, REAL, SECTION, STOP, TRUNCATE, RECOVER, SEGMENT, STORAGE, UNDER, REFERENCES, SEQUENCE, SUM, UNLIMITED, REFERENCING, SHARED, SWITCH, UNTIL, RESETLOGS, SNAPSHOT, SYSTEM, USE, RESTRICTED, SOME, TABLES, USING, REUSE, SORT, TABLESPACE, WHEN, ROLE, SQL, TEMPORARY, WRITE, ROLES, SQLCODE, THREAD, WORK, ROLLBACK, SQLERROR, TIME, ABORT, BETWEEN, CRASH, DIGITS, ACCEPT, BINARY_INTEGER, CREATE, DISPOSE, ACCESS, CURRENT, DISTINCT, ADD, BOOLEAN, CURRVAL, DO, ALL, BY, CURSOR, DROP, ALTER, CASE, DATABASE, ELSE, AND, DATA_BASE, ELSIF, ANY, CHAR_BASE, DATE, ARRAY, CHECK, ENTRY, ARRAYLEN, DEBUGOFF, EXCEPTION, AS, CLUSTER, DEBUGON, EXCEPTION_INIT, ASC, CLUSTERS, EXISTS, ASSERT, COLAUTH, DECIMAL, EXIT, ASSIGN, COLUMNS, DEFAULT, FALSE, AT, DEFINITION, COMPRESS, DELAY, FLOAT, DELETE, FOR, BASE_TABLE, CONSTANT, DELTA, FORM, DESC, FROM, FUNCTION, NEW, RELEASE, GENERIC, NEXTVAL, REMR, TABAUTH, GOTO, NOCOMPRESS, RENAME, TABLE, GRANT, NOT, RESOURCE, GROUP, NULL, RETURN, TASK, HAVING, NUMBER, REVERSE, TERMINATE, IDENTIFIED, NUMBER_BASE, REVOKE, IF, OF, ON, ROWID, TRUE, INDEX, ROWLABEL, TYPE, INDEXES, OPTION, ROWNUM, UNION, OR, ROWTYPE, UNIQUE, INSERT, ORDER, RUN, UPDATE, INTEGER, OTHERS, INTERSECT, OUT, VALUES, INTO, SELECT, VARCHAR, IS, PARTITION, SEPARATE, VARCHAR2, LEVEL, PCTFREE, SET, VARIANCE, LIKE, POSITIVE, SIZE, VIEW, LIMITED, PRAGMA, SMALLINT, VIEWS, LOOP, PRIOR, SPACE, WHERE, WHILE, MINUS, PUBLIC, SQLERRM, WITH, MLSLABEL, RAISE, MOD, RANGE, STATEMENT, XOR, MODE, STDDEV, NATURAL, RECORD, SUBTYPE
	}

	public static enum OracleDatatype {
		CHAR, VARCHAR2, VARCHAR, NCHAR, NVARCHAR2, CLOB, NCLOB, BLOB, BFILE, LONG, NUMBER, BINARY_FLOAT, BINARY_DOUBLE, DATE, TIMESTAMP, RAW, ROWID, UROWID, SMALLINT, DECIMAL, REAL, INT
	}
	
	public static enum SqlServerKeyword {
        ADD, EXTERNAL, PROCEDURE, ALL, FETCH, PUBLIC ,ALTER, FILE, RAISERROR, AND, FILLFACTOR, READ, ANY, FOR, READTEXT, AS, FOREIGN, RECONFIGURE, ASC, FREETEXT, REFERENCES, AUTHORIZATION, FREETEXTTABLE, REPLICATION, BACKUP, FROM, RESTORE, BEGIN, FULL, RESTRICT, BETWEEN, FUNCTION, RETURN, BREAK, GOTO, REVERT, BROWSE, GRANT, REVOKE, BULK, GROUP, RIGHT, BY, HAVING, ROLLBACK, CASCADE, HOLDLOCK, ROWCOUNT, CASE, IDENTITY, ROWGUIDCOL, CHECK, IDENTITY_INSERT, RULE, CHECKPOINT, IDENTITYCOL, SAVE, CLOSE, IF, SCHEMA, CLUSTERED, IN, SECURITYAUDIT, COALESCE, INDEX, SELECT, COLLATE, INNER, SEMANTICKEYPHRASETABLE, COLUMN, INSERT, SEMANTICSIMILARITYDETAILSTABLE, COMMIT,	INTERSECT,	SEMANTICSIMILARITYTABLE, COMPUTE, INTO, SESSION_USER, CONSTRAINT, IS, SET, CONTAINS, JOIN, SETUSER, CONTAINSTABLE, KEY, SHUTDOWN, CONTINUE, KILL, SOME, CONVERT, LEFT, STATISTICS, CREATE, LIKE, SYSTEM_USER, CROSS, LINENO, TABLE, CURRENT, LOAD, TABLESAMPLE, CURRENT_DATE, MERGE, TEXTSIZE, CURRENT_TIME, NATIONAL, THEN, CURRENT_TIMESTAMP, NOCHECK, TO, CURRENT_USER, NONCLUSTERED, TOP, CURSOR, NOT, TRAN, DATABASE, NULL, TRANSACTION, DBCC, NULLIF, TRIGGER, DEALLOCATE, OF, TRUNCATE, DECLARE, OFF, TRY_CONVERT, DEFAULT, OFFSETS, TSEQUAL, DELETE, ON, UNION, DENY, OPEN, UNIQUE, DESC, OPENDATASOURCE, UNPIVOT, DISK, OPENQUERY, UPDATE, DISTINCT, OPENROWSET, UPDATETEXT, DISTRIBUTED, OPENXML, USE, DOUBLE, OPTION, USER, DROP, OR, VALUES, DUMP, ORDER, VARYING, ELSE, OUTER, VIEW, END, OVER, WAITFOR, ERRLV, PERCENT, WHEN, ESCAPE, PIVOT, WHERE, EXCEPT, PLAN, WHILE, EXEC, PRECISION, WITH, EXECUTE, PRIMARY, WITHINGROUP, EXISTS, PRINT, WRITETEXT, EXIT, PROC
	}
	
	public static enum SqlServerDatatype {
		BIGINT, NUMERIC, BIT, SMALLINT, DECIMAL, SMALLMONEY, INT, TINYINT, MONEY, FLOAT, REAL, DATE, DATETIMEOFFSET, DATETIME2, SMALLDATETIME, DATETIME, TIME, CHAR,  VARCHAR, TEXT, NCHAR, NVARCHAR, NTEXT, BINARY, VARBINARY, IMAGE	
	}
	
	public static boolean checkIsEmpty(String key) {
		if (key == null || "".equals(key)) {
			LOGGER.debug(key + " : Table/Column name should not be empty or null");
			return true;
		}
		return false;
	}
	public static boolean checkSpecialChar(String key) {
		String specialCharacters = "[" + "-/@#!*$%^&~Â®â„¢*,:;|`.'_+={}()\\[\\]"+ "]+";
		if ( key.matches(specialCharacters)) {
			return true;
		}
		return false;
	}
	public static boolean checkKeyWord(String key) {
		if(dataBase.getProperty("spring.datasource.driver-class-name").contains("OracleDriver")){
		if(key == null)
			return false;
		LOGGER.debug(key+" Keyword match : "+ Stream.of(OracleKeyWord.values()).map(OracleKeyWord::name).anyMatch(e -> e.equalsIgnoreCase(key)));
		return Stream.of(OracleKeyWord.values()).map(OracleKeyWord::name).anyMatch(e -> e.equalsIgnoreCase(key));
		}
		else if(dataBase.getProperty("spring.datasource.driver-class-name").contains("SQLServerDriver")){
			if(key == null)
			return false;
		LOGGER.debug(key+" Keyword match : "+ Stream.of(SqlServerKeyword.values()).map(SqlServerKeyword::name).anyMatch(e -> e.equalsIgnoreCase(key)));
		return Stream.of(SqlServerKeyword.values()).map(SqlServerKeyword::name).anyMatch(e -> e.equalsIgnoreCase(key));
		}
		return false;
	}

	public static boolean checkSpecialCharacter(String key) {
		Pattern pattern = Pattern.compile("\\W+");
		Matcher matcher = pattern.matcher(key);

		while (matcher.find()) {
			LOGGER.debug(key + " : Matched special character(s) : " + matcher.group());
			return true;
		}
		return false;
	}
	public static boolean checkStartWithUnderscore(String key) {
		if (key != null && key.startsWith("_")) {
			LOGGER.debug(key + " : Table/Column/Datatype name should not start with underscore(_)");
			return true;
		}
		return false;
	}

	public static boolean checkdatatypeKeyWord(String datatype) {
		if(dataBase.getProperty("spring.datasource.driver-class-name").contains("OracleDriver") ){
		if(datatype == null)
			return false;
		LOGGER.debug(datatype+" Keyword match : "+ Stream.of(OracleDatatype.values()).map(OracleDatatype::name).noneMatch(e -> e.equalsIgnoreCase(datatype)));
		return Stream.of(OracleDatatype.values()).map(OracleDatatype::name).noneMatch(e -> e.equalsIgnoreCase(datatype));
		}
		else if(dataBase.getProperty("spring.datasource.driver-class-name").contains("SQLServerDriver") ){
			if(datatype == null)
			return false;
			LOGGER.debug(datatype+" Keyword match : "+ Stream.of(SqlServerDatatype.values()).map(SqlServerDatatype::name).noneMatch(e -> e.equalsIgnoreCase(datatype)));
			return Stream.of(SqlServerDatatype.values()).map(SqlServerDatatype::name).noneMatch(e -> e.equalsIgnoreCase(datatype));
		}
		return false;
	}

}
