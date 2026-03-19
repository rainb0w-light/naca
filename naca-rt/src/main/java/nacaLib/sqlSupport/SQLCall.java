/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.sqlSupport;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import jlib.log.Log;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbPreparedCallableStatement;
import jlib.sql.SQLTypeOperation;
import jlib.sql.StoredProcParamDescBase;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.varEx.Var;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLCall.java,v 1.4 2007/01/09 15:01:07 u930di Exp $
 */
public class SQLCall 
{
	private String csStoredProcName = null;
	private BaseProgramManager programManager = null;
	private StoredProcParams arrStoredProcParams = null;
	private DbConnectionBase sQLConnection = null;
	private int nNbParamToProvide = -1; 	// Number of para to provide to the stored proc
	private int nNbParamProvided = 0;	// Number of parameters provided by application
	//private ArrayList<Var> arrInOutParam = null;
	private PreparedCallableStatement preparedCallableStatement = null;
	CSQLStatus sqlStatus = null;
	private SQLErrorManager errorManager = null;
		
	public SQLCall(BaseProgramManager programManager, String csStoredProcName)
	{
		this.programManager = programManager;
		this.csStoredProcName = csStoredProcName;
		errorManager = new SQLErrorManager();
		create();
	}
	
	private void create()
	{
		sqlStatus = new CSQLStatus();
		
		// Determine number and way of proc params
		StoredProcSupport sp = new StoredProcSupport();
		
		BaseEnvironment env = programManager.getEnv();
		sQLConnection = env.getSQLConnection();
		
		if(sQLConnection != null)
		{
			arrStoredProcParams = sp.getStoredProcedureParamsList(sQLConnection, csStoredProcName);
			if(arrStoredProcParams != null)
				nNbParamToProvide = arrStoredProcParams.getNbParamToProvide();
			manageOperationEnding();
		}
	}
	
	public SQLCall param(int nParamId, Var var)
	{
		nParamId--;	// 0 based
		if(arrStoredProcParams != null && nParamId < arrStoredProcParams.getNbParamToProvide())
		{
			StoredProcParamDesc storedProcParamDesc = arrStoredProcParams.get(nParamId);
			storedProcParamDesc.setVar(var);
		}
		nNbParamProvided++;
		
		manageOperationEnding();
		return this;
	}

	// Fake methods
	public SQLCall onErrorGoto(Paragraph paragraphSQGErrorGoto)
	{
		errorManager.manageOnErrorGoto(paragraphSQGErrorGoto, sqlStatus);
		return this;
	}

	public SQLCall onErrorGoto(Section section)
	{
		errorManager.manageOnErrorGoto(section, sqlStatus);
		return this;	
	}

	public SQLCall onErrorContinue()
	{
		errorManager.manageOnErrorContinue(sqlStatus);
		return this;
	}

	public SQLCall onWarningGoto(Paragraph paragraphSQGErrorGoto)
	{
		// TODO
		return this;
	}

	public SQLCall onWarningGoto(Section section)
	{
		// TODO
		return this;
	}

	public SQLCall onWarningContinue()
	{
		// TODO
		return this;
	}


	private void manageOperationEnding()
	{
		if (sQLConnection != null)
		{
			if (nNbParamToProvide == nNbParamProvided) // All paraqm have been provided
			{
				if(prepareCallableStatement())
				{
					execute();
					retrieveOutValues();
					// read out params and write value in destination vars
					close();
				}
			}
		}
	}

	private boolean prepareCallableStatement()
	{
		preparedCallableStatement = new PreparedCallableStatement(null);
		boolean bPrepared = sQLConnection.prepareCallableStatement(preparedCallableStatement, csStoredProcName, nNbParamToProvide);
		if(bPrepared)
		{
			return arrStoredProcParams.registerInOutParameters(preparedCallableStatement);
		}
		return false;
	}
	
	private void retrieveOutValues()
	{
		if(preparedCallableStatement != null)
		{
			arrStoredProcParams.retrieveOutValues(preparedCallableStatement, sqlStatus);
		}
	}
	
	private void execute()
	{
		sqlStatus.reset();
		try
		{
			if(preparedCallableStatement != null)
			{
				boolean b = preparedCallableStatement.execute();
			}
		}
		catch(SQLException e)
		{
			//String cs = e.getCause();
			String csState = e.getSQLState();
			String csReason = e.getMessage();
			Log.logImportant("Catched SQLException from stored procedure: "+csReason + " State="+csState);
			String csSPName = "StoredProc:" + csStoredProcName;
			sqlStatus.setSQLCode(csSPName, e.getErrorCode(), csReason, csState);
		}
	}
	
	private boolean close()
	{
		if(preparedCallableStatement != null)
			return preparedCallableStatement.close();
		return false;
	}
}
