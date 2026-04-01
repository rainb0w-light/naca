/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import java.sql.ResultSet;
import java.sql.SQLException;

import jlib.sql.*;
import jlib.xml.*;

/*
DB Table on MySQL
// Table Header
CREATE TABLE 'logheader' (
  'Id' int(10) unsigned NOT NULL auto_increment,
  'Ins_Date' timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  'Type' int(10) unsigned NOT NULL default '0',
  'File' varchar(255) character set latin1 collate latin1_bin default NULL,
  'Line' int(10) unsigned NOT NULL default '0',
  'Thread' varchar(45) NOT NULL default '',
  'Method' varchar(255) NOT NULL default '',
  'StartTime' int(10) unsigned NOT NULL default '0',
  'EventName' varchar(255) NOT NULL default '',
  'Message' text NOT NULL,
  PRIMARY KEY  ('Id')
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

// Table Details
CREATE TABLE 'logdetails' (
  'Id' int(10) unsigned NOT NULL default '0',
  'DetailId' int(10) unsigned NOT NULL auto_increment,
  'Name' varchar(255) NOT NULL default '',
  'Value' text NOT NULL,
  PRIMARY KEY  ('DetailId')
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
*/

public class LogCenterDb extends LogCenter
{
	public LogCenterDb(LogCenterLoader logCenterLoader)
	{
		super(logCenterLoader);
	}
	
//	private boolean normalizeAppend(String cs)
//	{
//		if(cs.equalsIgnoreCase("false"))
//			return false;
//		else if(cs.equalsIgnoreCase("0"))
//			return false;
//		return true;		
//	}
	
	private String csMasterTable = null;
	private String csDetailsTable = null;
	private String csDbUser = null;
	private String csDbPassword = null;
	private String csDbUrl = null;
	private String csDbProvider = null;
	private boolean isuseSequence = false;
	DbConnectionManager manager = null;
	DbConnectionBase dbConnection = null;
		
	public void loadSpecificsEntries(Tag tagLogCenter)	// Special values for file appenders
	{
		csDbUser = tagLogCenter.getVal("DbUser");
		csDbPassword = tagLogCenter.getVal("DbPassword");
		csDbUrl = tagLogCenter.getVal("DbUrl");
		csDbProvider = tagLogCenter.getVal("DbProvider");
		csMasterTable = tagLogCenter.getVal("MasterTable");
		csDetailsTable = tagLogCenter.getVal("DetailsTable");
	}
	
	boolean open()
	{
		boolean b = false;
		int nTime_Ms = 1000 * 60 * 10;		// 10 minutes
		manager = new DbConnectionManager();
		if(csDbProvider.equalsIgnoreCase("MySql"))
		{
			b = manager.initMySql(csDbUrl, csDbUser, csDbPassword, null, 2, nTime_Ms, -1, 0);
			isuseSequence = false;
		}
		else if(csDbProvider.equalsIgnoreCase("Oracle"))
		{
			b = manager.initOracle(csDbUrl, csDbUser, csDbPassword, null, 2, nTime_Ms, -1, 0);
			isuseSequence = false;
		}
		else if(csDbProvider.equalsIgnoreCase("SqlServer"))
		{
			b = manager.initSqlServer(csDbUrl, csDbUser, csDbPassword, null, 2, nTime_Ms, -1, 0);
			isuseSequence = false;
		}
		else if(csDbProvider.equalsIgnoreCase("DB2"))
		{
			b = manager.initDB2(csDbUrl, csDbUser, csDbPassword, null, 2, nTime_Ms, -1, 0);
			isuseSequence = false;
		}
		else
		{
			b = manager.initDriverClass(csDbUrl, csDbUser, csDbPassword, null, csDbProvider, 2, nTime_Ms, -1, 0);
			isuseSequence = false;
		}
			
		return b;
	}
	
	boolean closeLogCenter()
	{
		return true;
	}
	
	void preSendOutput()
	{
		try
		{
			dbConnection = manager.getConnection("LogStatement", null, true);
		}
		catch (DbConnectionException e)
		{
			e.printStackTrace();
			dbConnection = null;
		}	
	}

	void sendOutput(LogParams logParam)
	{	
		// Insert header
		if(dbConnection != null)
		{
			String cs;
			
			if(!isuseSequence)
				cs = "Insert into " + csMasterTable + " (" +
					"Log_Type, File_Name, Line, Thread, Method, Start_Time, Event_Name, Message) Values (" +
					"?,    ?,    ?,    ?,      ?,      ?,         ?,         ?)";
			else
				cs = "Insert into " + csMasterTable + " (" +
					"Log_Type, File_Name, Line, Thread, Method, Start_Time, Event_Name, Message, Id) Values (" +
					"?,    		?,    		?,    ?,      ?,      ?,         ?,         ?, 		 SEQ_LOG_ID.nextval)";
				 
			int nCol = 0;
			DbPreparedStatement stInsertHeader = dbConnection.prepareStatement(cs, 0, false);
			stInsertHeader.setColParam(nCol++, logParam.getType());
			stInsertHeader.setColParam(nCol++, logParam.getFile());
			stInsertHeader.setColParam(nCol++, logParam.getLine());
			stInsertHeader.setColParam(nCol++, logParam.getThreadName());
			stInsertHeader.setColParam(nCol++, logParam.getMethod());
			stInsertHeader.setColParam(nCol++, logParam.getStartTime());
			stInsertHeader.setColParam(nCol++, logParam.getEventName());
			stInsertHeader.setColParam(nCol++, logParam.getMessage());
			
			
			int n0 = stInsertHeader.executeInsert();
			
			long lastId = 0;
			cs = "SELECT Id FROM " + csMasterTable + " order by Id desc";
			DbPreparedStatement stSelectLastId = dbConnection.prepareStatement(cs, 0, false);
			ResultSet resultSet = stSelectLastId.executeSelect();
			if(resultSet != null)
			{
				try
				{
					resultSet.next();
					lastId = resultSet.getLong(1);
				} 
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//resultSet.close();	// TBD
			}
			
			if(!isuseSequence)
				cs = "Insert into " + csDetailsTable + " (Id, Name, Value) Values (?,  ?,    ?)";
			else
				cs = "Insert into " + csDetailsTable + "(Id, Name, Value, Detail_Id) Values (?, ?, ?, SEQ_LOGDETAIL_ID.nextval)";
			
			int nNbMembers = logParam.getNbParamInfoMember();
			
			DbPreparedStatement insDetails = dbConnection.prepareStatement(cs, 0, false);
			for(int nMember=0; nMember<nNbMembers; nMember++)
			{
				LogInfoMember member = logParam.getParamInfoMember(nMember);
				if(member != null)
				{					 
					nCol = 0;
					
					insDetails.setColParam(nCol++, lastId);
					insDetails.setColParam(nCol++, member.getName());
					insDetails.setColParam(nCol++, member.getValue());
						
					insDetails.executeInsert();
				}			
			}
		}
	}
	
	void postSendOutput()
	{
		if(dbConnection != null)
		{
			dbConnection.commit();
			dbConnection.returnConnectionToPool();
		}
	}
	
	public String getType()
	{
		return "LogCenterDb";
	}
}