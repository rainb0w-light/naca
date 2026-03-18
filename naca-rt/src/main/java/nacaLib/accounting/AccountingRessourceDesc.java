/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.accounting;

import jlib.log.Log;
import jlib.misc.StringUtil;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbConnectionException;
import jlib.sql.DbConnectionPool;
import jlib.sql.DbPreparedStatement;
import jlib.xml.Tag;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.sqlSupport.SQLConnectionManager;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: AccountingRessourceDesc.java,v 1.13 2007/02/19 16:44:49 u930di Exp $
 */
public class AccountingRessourceDesc
{
	public AccountingRessourceDesc()
	{
	}
	
	public void load(Tag tagAccounting)
	{
		if(tagAccounting != null)
		{
			csTableName = tagAccounting.getVal("TableName");
			csMachineId = tagAccounting.getVal("MachineId");
			csTomcatId = tagAccounting.getVal("TomcatId");
			connectionManager = new SQLConnectionManager();
			DbConnectionPool dbConnectionPool = connectionManager.init("", tagAccounting);
			BaseResourceManager.addDbConnectionPool(dbConnectionPool);
			nMaxLevelDepth = tagAccounting.getValAsInt("MaxLevelDepth");
			String csDbEnvironment = tagAccounting.getVal("dbenvironment");
			if(csDbEnvironment != null && !StringUtil.isEmpty(csDbEnvironment))
				csTableName = csDbEnvironment + "." + csTableName;
			csInsertClause = "Insert into " + csTableName +
				"(SESSIONID, TRANSACTIONID, START_TIMESTAMP, LEVEL_DEPTH, TRANSACTIONNAME, PROGRAMNAME, SESSIONTYPE, MACHINEID, TOMCATID, RUNTIME_MS, TERMINALID, LUNAME, USERLDAPID, CRITERIAEND, NBSELECT, NBINSERT, NBUPDATE, NBDELETE, NBOPENCURSOR, NBFETCHCURSOR, PROFITCENTERP2000, USERIDP2000, DB_IO_TIME_MS, NETWORK_MS)" + 
				" values " +
				"(?, ?, ?, ?, ?, ?,	?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,	?, ?, ?, ?,	?, ?)";
		}
		else
		{
			Log.logCritical("No Accounting tag in .cfg file: Accouting is disabled");
		}
	}
	
	String getMachineId()
	{
		return csMachineId;
	}
	
	String getTomcatId()
	{
		return csTomcatId;
	}
	
	boolean canWrite(int nCurrentDepth)
	{
		if(nCurrentDepth <= nMaxLevelDepth)
			return true;
		return false;
	}
	
	
	DbConnectionBase getConnection()
	{
		if(connectionManager != null)
		{
			try
			{
				DbConnectionBase dbConnection = connectionManager.getConnection("Accounting", true);
				return dbConnection;
			}
			catch (DbConnectionException e)
			{
				Log.logCritical("Could not get DB connection for accounting !");
			}
		}
		return null;
	}
	
	DbPreparedStatement getInsertStatement(DbConnectionBase dbConnection)
	{
		DbPreparedStatement st = dbConnection.prepareStatement(csInsertClause, 0, false);
		return st;
	}
	
	void returnConnection(DbConnectionBase dbConnection)
	{
		connectionManager.returnConnection(dbConnection);
	}

	private SQLConnectionManager connectionManager = null;
	private String csTableName = null;
	private String csMachineId = null;
	private String csTomcatId = null;
	private int nMaxLevelDepth = 0;
	private String csInsertClause = null;
}
