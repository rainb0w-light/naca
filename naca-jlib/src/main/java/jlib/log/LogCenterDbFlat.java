/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import jlib.sql.DbConnectionBase;
import jlib.sql.DbConnectionException;
import jlib.sql.DbConnectionManager;
import jlib.sql.DbPreparedStatement;
import jlib.xml.Tag;
/**
 * Log center saving events into a flat database.
 * This log center is used to keep record of applications execution cycle:
 * <ul>
 * 	<li>The application starts.</li>
 * 	<li>Eventually the application launches another application to perform some
 * 	additional task.</li>
 * 	<li>The application processes elements of information.</li>
 * 	<li>Eventually the application founds an error in some of the information 
 * 	elements to process. Then it skips the current element to process the next
 * 	one.</li>
 * 	<li>Eventually, the error is so critical that the application has to
 * 	abort prematurely.</li>
 * 	<li>Application reports the amount of processed information elements.</li>
 * 	<li>Application finishes.</li>
 * </ul>
 * Events logged through this class are thereafter visible with the
 * COP/Log interface (http://c930cop.consultas.ch/LOG).
 * For the COP/LOG interface to be able to group events by execution cycles, and to
 * keep track of applications launching child applications, some identifiers are needed.
 * <ul>
 * 	<li><i>Process</i> is the internal application name. The term "Application Name"
 * 	has to be used in its most general sense.</li>
 * 	<li><i>RuntimeId</i> is a unique identifier retrieved when the application starts,
 * 	and kept during all the application execution cycle. It is used to stamp all
 * 	events raised by the application.</li>
 * 	<li><i>RunId</i> is a unique identifier retrieved when the application starts,
 * 	much like <i>RuntimeId</i>. The difference is that, if the application launches
 * 	a child application, it will pass the <i>RunId</i> identifier. It is used to stamp all
 * 	events raised by the application, plus all events raised by the child applications.</li>
 * </ul>
 * The COP/Log interface produces two types of reports:
 * <ul>
 * 	<li>Execution reports, that show when an application starts, stops, how
 * 	many information elements it has processed, how many exceptions has been raised,
 * 	how much time it has been running, etc.</li>
 * 	<li>Commercial reports, that show how many elements of a particular client, product,
 * 	brand, source, etc. has been processed in general, or by a particular application.</li>
 * </ul>
 * For that second kind of report, an additional identifier is needed to specify the
 * client, product, brand, source, etc. This additional identifier is the <i>Product</i>.<p/>
 * 
 * This is a typical <i>LogCenterDbFlat</i> configuration:
 * <pre>
 * </pre>
 * This configuration predefines the default <i>Process</i> name, and the default <i>Product</i> 
 * name. That means that any {@link LogEvent} not specifying 
 * the {@link LogEvent#getProcess} or the {@link LogEvent#getProduct}, will be assigned to
 * the default ones if the event is accepted by the log center.  
 */

public class LogCenterDbFlat extends LogCenter
{
	public LogCenterDbFlat(LogCenterLoader logCenterLoader)
	{
		super(logCenterLoader);
	}
	
	private String csTable = null;
	private String csTableRunId = null;
	private String csLogEventDefinitionTable = null;
	private String csDbUser = null;
	private String csDbPassword = null;
	private String csDbUrl = null;
	private String csDbProvider = null;
	private String csMachine = null;
	private String csRunMode = null;
	private Hashtable<Integer, Boolean> hashDefinedLogEvent = new Hashtable<Integer, Boolean> ();
	
	DbConnectionManager manager = null;
	DbConnectionBase dbConnection = null;
		
	public void loadSpecificsEntries(Tag tagLogCenter)	// Special values for file appenders
	{
		csDbUser = tagLogCenter.getVal("DbUser");
		csDbPassword = tagLogCenter.getVal("DbPassword");
		csDbUrl = tagLogCenter.getVal("DbUrl");
		csDbProvider = tagLogCenter.getVal("DbProvider");
		csTable = tagLogCenter.getVal("Table");
		csTableRunId = tagLogCenter.getVal("TableRunId");
		csMachine = tagLogCenter.getVal("Machine");
		csProcess = tagLogCenter.getVal("Process");
		csRunMode = tagLogCenter.getVal("RunMode");
		csLogEventDefinitionTable = tagLogCenter.getVal("LogEventDefinitionTable");
	}
	
	boolean open()
	{
		boolean b = false;
		int nTime_Ms = 1000 * 60 * 10;		// 10 minutes
		manager = new DbConnectionManager();

		if(csDbProvider.equalsIgnoreCase("MySql"))
		{
			b = manager.initMySql(csDbUrl, csDbUser, csDbPassword, null, 8, nTime_Ms, -1, 0);
		}
		else if(csDbProvider.equalsIgnoreCase("Oracle"))
		{
			b = manager.initOracle(csDbUrl, csDbUser, csDbPassword, null, 8, nTime_Ms, -1, 0);
		}
		else if(csDbProvider.equalsIgnoreCase("SqlServer"))
		{
			b = manager.initSqlServer(csDbUrl, csDbUser, csDbPassword, null, 8, nTime_Ms, -1, 0);
		}
		else if(csDbProvider.equalsIgnoreCase("DB2"))
		{
			b = manager.initDB2(csDbUrl, csDbUser, csDbPassword, null, 8, nTime_Ms, -1, 0);
		}
		else
		{
			b = manager.initDriverClass(csDbUrl, csDbUser, csDbPassword, null, csDbProvider, 8, nTime_Ms, -1, 0);
		}
		
		try
		{
			dbConnection = manager.getConnection("LogStatement", null, true);
		}
		catch (DbConnectionException e)
		{
			e.printStackTrace();
		}
		if(dbConnection != null)
			loadDefinedLogEvent();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbConnection = null;
		}	
	}

	void sendOutput(LogParams logParam)
	{		
		if(dbConnection != null)
		{
			int nEventId = logParam.getEventId();
			manageLogEventDefinition(logParam, nEventId);

			String csRunId=logParam.csRunId;
			if (csRunId==null)
				csRunId=getRunId();

			String csRuntimeId=logParam.csRuntimeId;
			if (csRuntimeId==null)
				csRuntimeId=getRuntimeId();

			String csProduct=logParam.logEvent.getProduct();
			if (csProduct==null)
				csProduct=getProduct();

			String csProcess=logParam.logEvent.getProcess();
			if (csProcess==null)
				csProcess=getProcess();

			String csParamNames = "";
			String csParamQuestions = "";
			int nNbParam = logParam.getNbParamInfoMember();
			for(int n=0; n<nNbParam; n++)
			{
				csParamNames += ", Parameter_Value" + n;
				csParamQuestions += ", ?";
			}
			
			String cs = "Insert into " + csTable +
				"(Machine, Process, Run_Mode, Ins_Date, Event_Message, Log_Type, File_Name, Line, Thread, Method, Start_Time, Event_Id, Run_Id, Product, Runtime" + csParamNames +  
				") values (" +  
				" ?,       ?,       ?,        ?,        ?,             ?,        ?,         ?,    ?,      ?,      ?,          ?,        ?,      ?,       ?" + csParamQuestions + 
				")";   
			int nCol = 0;
			DbPreparedStatement stInsert = dbConnection.prepareStatement(cs, 0, false);
			stInsert.setColParam(nCol++, csMachine);
			stInsert.setColParam(nCol++, csProcess);
			stInsert.setColParam(nCol++, csRunMode);
			stInsert.setColParam(nCol++, logParam.getDisplayTimestamp());
			stInsert.setColParam(nCol++, logParam.getMessage());
			stInsert.setColParam(nCol++, logParam.getType());
			stInsert.setColParam(nCol++, logParam.getFile());
			stInsert.setColParam(nCol++, logParam.getLine());
			stInsert.setColParam(nCol++, logParam.getThreadName());
			stInsert.setColParam(nCol++, logParam.getMethod());
			stInsert.setColParam(nCol++, logParam.getStartTime());
			stInsert.setColParam(nCol++, nEventId);
			stInsert.setColParam(nCol++, csRunId);
			stInsert.setColParam(nCol++, csProduct);
			stInsert.setColParam(nCol++, csRuntimeId);
	//		Runtime rt = Runtime.getRuntime();
	//		String csRuntime = rt.toString();
	//		stInsert.setColParam(nCol++, csRuntime);
			for(int n=0; n<nNbParam; n++)
			{
				String csParam = logParam.getItemValue(n);
				stInsert.setColParam(nCol++, csParam);
			}
			
			int n0 = stInsert.executeInsert();
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
	
	private void loadDefinedLogEvent()
	{
		String cs = "Select Event_Id from " + csLogEventDefinitionTable; 
		DbPreparedStatement st = dbConnection.prepareStatement(cs, 0, false);
		ResultSet rs = st.executeSelect();
		if(rs != null)
		{
			boolean isnext;
			try
			{
				isnext = rs.next();
				while(isnext)
				{
					Integer i = rs.getInt("Event_Id");
					hashDefinedLogEvent.put(i, new Boolean(true));
					isnext = rs.next();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void manageLogEventDefinition(LogParams logParam, int nEventId)
	{
		Boolean b = hashDefinedLogEvent.get(nEventId);
		if(b == null)	// Log event not defined yet
		{
			boolean isinserted = addLogEventDefinition(nEventId, logParam);
			if(isinserted)
			{
				hashDefinedLogEvent.put(nEventId, new Boolean(true));
			}
		}
	}
	
	private boolean addLogEventDefinition(int nEventId, LogParams logParam)
	{
		String csParamNames = "";
		String csParamQuestions = "";
		int nNbParam = logParam.getNbParamInfoMember();
		for(int n=0; n<nNbParam; n++)
		{
			csParamNames += ", Parameter_Name" + n;
			csParamQuestions += ", ?";
		}
		
		String cs = "Insert into " + csLogEventDefinitionTable +
			"(Event_Name, Event_Id, Short_Event_Name" + csParamNames +    
			") values (" +  
			"?,           ?,         ?" + csParamQuestions + ")";  
			   
		int nCol = 0;
		DbPreparedStatement stInsert = dbConnection.prepareStatement(cs, 0, false);
		stInsert.setColParam(nCol++, logParam.getEventName());
		stInsert.setColParam(nCol++, nEventId);
		stInsert.setColParam(nCol++, logParam.getShortEventName());
		for(int n=0; n<nNbParam; n++)
		{
			LogInfoMember info = logParam.getParamInfoMember(n);
			stInsert.setColParam(nCol++, info.getName());
		}
		
		int n = stInsert.executeInsert();
		if(n == 1)
			return true;
		return false;
	}
/**
 * Returns the current <i>RunId</i> identifier.
 * If it is not set (or has been set to <i>null</i>), creates a new
 * unique identifier.
 */
	public String getRunId() {
		if (csRunId==null) 
		{
			csRunId=generateIdentifier();
		}
		return csRunId;
	}

/**
 * Returns the current <i>RuntimeId</i> identifier.
 * If it is not set (or has been set to <i>null</i>), creates a new
 * unique identifier.
 */
	public String getRuntimeId() {
		if (csRuntimeId==null) 
		{
			csRuntimeId=generateIdentifier();
		}
		return csRuntimeId;
	}

	synchronized private String generateIdentifier()
	{
		String csOut = "0";
		DbConnectionBase dbConnection;
		try 
		{
			dbConnection=manager.getConnection("LogStatement", null, true);
		}
		catch (DbConnectionException e) 
		{
			e.printStackTrace();
			return csOut;
		}
	
		String cs = "Select RunId from " + csTableRunId + " where channel=''";	
		DbPreparedStatement stSelect = dbConnection.prepareStatement(cs, 0, false);
		ResultSet rs = stSelect.executeSelect();
		if (rs==null) {
			try {
				System.out.println(dbConnection.getDbConnection().getWarnings().getMessage());
			} catch (SQLException s) {
				System.out.println(s.getMessage());
			}
		}
		if(rs != null)
		{
			boolean isnext;
			try
			{
				isnext = rs.next();
				if(isnext)
				{
					int nRunId = rs.getInt("RunId");
					nRunId++;
//					stSelect.close();
//					String csUpdate = "update " + csTableRunId + " set RunId=" + nRunId + " where channel='"+csOrganisation+"'";		
					String csUpdate = "update " + csTableRunId + " set RunId=" + nRunId + " where channel=''";		
					DbPreparedStatement stUpdate = dbConnection.prepareStatement(csUpdate, 0, false);
					int n = stUpdate.executeUpdate();
					csOut = String.valueOf(nRunId);
					stUpdate.close();
				}
				else	// 1st record
				{
//					stSelect.close();
					String csInsert = "Insert into " + csTableRunId + " (Channel, RunId) values (?, ?)";
					DbPreparedStatement stInsert = dbConnection.prepareStatement(csInsert, 0, false);
//					stInsert.setColParam(0, csOrganisation);
					stInsert.setColParam(0, "");
					stInsert.setColParam(1, 1);
					int n = stInsert.executeInsert();
					csOut = "1";
					stInsert.close();
				}
			}
			catch (SQLException e)
			{
				stSelect.close();
			}
		}
		dbConnection.commit();
		dbConnection.returnConnectionToPool();
		return csOut;
	}
	
	public String getType()
	{
		return "LogCenterDbFlat";
	}
}

