/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.calledPrgSupport;

import jlib.sql.DbConnectionBase;
import nacaLib.accounting.CriteriaEndRunMain;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.callPrg.CalledProgramLoader;
import nacaLib.callPrg.CalledResourceManager;
import nacaLib.callPrg.CalledResourceManagerFactory;
import nacaLib.callPrg.CalledSession;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.exceptions.ProgramCallerException;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ProgramCallerWithArgsPosition.java,v 1.2 2007/09/21 15:11:30 u930bm Exp $
 */
public class ProgramCallerWithArgsPosition extends CalledProgramParamSupportByPosition
{
	private String csConfigFile = null;
	private DbConnectionBase dbConnection = null;
	private String csPrgClassName = null;
	
	public ProgramCallerWithArgsPosition(String csConfigFile, DbConnectionBase dbConnection, String csPrgClassName)
	{
		this.csConfigFile = csConfigFile;
		this.dbConnection = dbConnection;
		this.csPrgClassName = csPrgClassName;
	}
	public ProgramCallerWithArgsPosition(String csConfigFile, DbConnectionBase dbConnection, Class classPrgToCall)
	{
		this.csConfigFile = csConfigFile;
		this.dbConnection = dbConnection;
		this.csPrgClassName = classPrgToCall.getName();
	}

	public boolean execute() throws ProgramCallerException
	{
		CalledResourceManager calledResourceManager = CalledResourceManagerFactory.GetInstance(csConfigFile, dbConnection.getEnvironmentPrefix());
		if(calledResourceManager == null)
			return false;
		
		CalledSession session = new CalledSession(calledResourceManager) ;
			
		BaseEnvironment env = null;
		try
		{
			BaseProgramLoader loader = CalledProgramLoader.GetProgramLoaderInstance() ;
			env = loader.GetEnvironment(session, csPrgClassName, null) ;
			env.setExternalDbConnection(dbConnection);
			boolean bUseStatementCache = BaseResourceManager.getUseStatementCache();
			env.fillEnvConnectionWithAllocatedConnection(dbConnection.getDbConnection(), "ExternalConnection", dbConnection.getEnvironmentPrefix(), bUseStatementCache);							
	
			boolean isstarted = env.startRunTransaction();
			if(!isstarted)
			{
				env.endRunTransaction(CriteriaEndRunMain.Abort);
				return false;
			}
				
			loader.runTopProgram(env, arrPublicArgs);
			
			env.endRunTransaction(CriteriaEndRunMain.Normal);
			return true;
		}
		catch (AbortSessionException e)
		{
			env.endRunTransaction(CriteriaEndRunMain.Abort);
			String csMessage = e.getReason();
			ProgramCallerException callerException = new ProgramCallerException(csMessage);
			throw callerException;
		}
		catch(Exception e)
		{
			env.endRunTransaction(CriteriaEndRunMain.Abort);
			String csMessage = e.getMessage();
			ProgramCallerException callerException = new ProgramCallerException(csMessage);
			throw callerException;
		}
	}
}
