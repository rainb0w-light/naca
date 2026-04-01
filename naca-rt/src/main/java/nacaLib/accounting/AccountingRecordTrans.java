/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.accounting;

import java.sql.Date;
import java.util.NoSuchElementException;
import java.util.Stack;

import jlib.log.Log;
import jlib.misc.StopWatchNano;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbPreparedStatement;
import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.CurrentUserInfo;

public class AccountingRecordTrans
{
	private BaseResourceManager baseResourceManager = null;
	
	public AccountingRecordTrans(BaseResourceManager baseResourceManager)
	{
		this.baseResourceManager = baseResourceManager;
		accountingRessourceDesc = this.baseResourceManager.getAccountingRessourceDesc();
		if(accountingRessourceDesc != null)
		{
			csMachineId = accountingRessourceDesc.getMachineId();
			csTomcatId = accountingRessourceDesc.getTomcatId();
			nUniqueSessionRequestId = baseResourceManager.getUniqueSessionRequestId();
		}
	}
	
	public void startRunTransaction(String csCurrentTransaction)
	{
		isfilled = false;
		nTransactionId++;
		nNbSelect = 0;
		nNbInsert = 0;
		nNbUpdate = 0;
		nNbDelete = 0;
		nNbFetchCursor = 0;
		nNbCursorOpen = 0;
		swnDbTimeRunTransaction.reset();
		//JmxGeneralStat.startRunTransaction();
		
		createNewAccountingRecord(csCurrentTransaction, "");
	}

	public void endRunTransaction(String csCurrentTransaction, CriteriaEndRunMain criteria)
	{
		if(BaseResourceManager.getUsingJmx())
		{
			long runtimeTrans_ns = swnDbTimeRunTransaction.getElapsedTime();
			JmxGeneralStat.endRunTransaction(criteria, runtimeTrans_ns / 1000000, sumDbTimeIO_ns / 1000000);
		}
		
		endRunProgram(criteria);
	}

	public AccountingRecordProgram createNewAccountingRecord(String csCurrentTransaction, String csTermId)
	{
		this.csCurrentTransaction = csCurrentTransaction;
		this.csTerminalId = csTermId;
		AccountingRecordProgram accountingRecord = new AccountingRecordProgram();
		accountingStack.push(accountingRecord);
		return accountingRecord;
	}
	
	public void endRunProgram(CriteriaEndRunMain criteria)
	{
		if(accountingRessourceDesc != null)
		{
			AccountingRecordProgram accountingRecordProgram = accountingStack.pop();
			int nDepthLevel = accountingStack.size();
			if(accountingRessourceDesc.canWrite(nDepthLevel))
			{
				accountingRecordProgram.endRunProgram(criteria);
				write(accountingRecordProgram, nDepthLevel);
			}
		}
	}
	
	public void write(AccountingRecordProgram accountingRecordProgram, int nDepthLevel)
	{
		DbConnectionBase dbConnection = accountingRessourceDesc.getConnection();
		if(dbConnection != null)
		{
			DbPreparedStatement stInsert = accountingRessourceDesc.getInsertStatement(dbConnection);
			if(stInsert != null)
			{
				try
				{
					int nCol = 0;
					
					stInsert.setColParam(nCol++, nUniqueSessionRequestId);	// INTEGER SESSIONID
					stInsert.setColParam(nCol++, nTransactionId);	// TRANSACTIONID
		
					long l = accountingRecordProgram.getTimeDateStart();
					Date date = new Date(l);
					stInsert.setColParam(nCol++, date);	// START_TIMESTAMP
		
					stInsert.setColParam(nCol++, nDepthLevel);	// LEVEL_DEPTH
					stInsert.setColParam(nCol++, csCurrentTransaction);	// TRANSACTIONNAME
					String csProg = accountingRecordProgram.getProgramName();
					if(csProg.length() > 8)
						csProg = csProg.substring(0, 8);			
					stInsert.setColParam(nCol++, csProg);	// PROGRAMNAME
					stInsert.setColParam(nCol++, csSessionType);	// SESSIONTYPE
					stInsert.setColParam(nCol++, csMachineId);	// MACHINEID
					stInsert.setColParam(nCol++, csTomcatId);	// TOMCATID
					stInsert.setColParam(nCol++, accountingRecordProgram.getRunTime_ms());	// RUNTIME_MS
					stInsert.setColParam(nCol++, csTerminalId);	// TERMINALID
					stInsert.setColParam(nCol++, currentUserInfo.csLUName);	// LUNAME
					stInsert.setColParam(nCol++, currentUserInfo.csUserLdapId);	// USERLDAPID
					stInsert.setColParam(nCol++, accountingRecordProgram.getCriteriaEnd());	// CRITERIAEND
					stInsert.setColParam(nCol++, nNbSelect);	// NBSELECT
					stInsert.setColParam(nCol++, nNbInsert);	// NBINSERT, 
					stInsert.setColParam(nCol++, nNbUpdate);	// NBUPDATE, 
					stInsert.setColParam(nCol++, nNbDelete);	// NBDELETE, 
					stInsert.setColParam(nCol++, nNbCursorOpen);	// NBOPENCURSOR, 
					stInsert.setColParam(nCol++, nNbFetchCursor);	// NBFETCHCURSOR, 
					stInsert.setColParam(nCol++, currentUserInfo.csPub2000ProfitCenter);	// PROFITCENTERPUB2000, 
					stInsert.setColParam(nCol++, currentUserInfo.csPub2000UserId);	// USERIDPUB2000
					stInsert.setColParam(nCol++, StopWatchNano.getMilliSecond(accountingRecordProgram.getRunTimeIO_ns()));
					stInsert.setColParam(nCol++, nNetwork_ms);
					int n = stInsert.executeInsert();
					if(n != 1)
					{
						Log.logCritical("Could not insert accounting record");
					}
				}
				catch(Exception e)
				{
					Log.logCritical("Could not insert accounting record, because of exception " + e.getMessage());
				}
			}
			accountingRessourceDesc.returnConnection(dbConnection);
		}		
	}
	
	
	public void incDelete()
	{
		nNbDelete++;
	}
	
	public void incSelect()
	{
		nNbSelect++;
	}

	public void incCursorOpen()
	{
		nNbCursorOpen++;
	}
	
	public void incFetchCursor()
	{
		nNbFetchCursor++;
	}

	public void incUpdate()
	{
		nNbUpdate++;
	}
	
	public void incInsert()
	{
		nNbInsert++;
	}
	
	public void startDbIO()
	{
		swnDbTimeIO.reset();
	}

	public void endDbIO()
	{
		lDbTimeIO_ns = swnDbTimeIO.getElapsedTimeReset();
		sumDbTimeIO_ns += lDbTimeIO_ns;
		//JmxGeneralStat.reportDbTimeIo_ns(lDbTimeIO_ns / 1000000);
		try
		{
			AccountingRecordProgram prg = accountingStack.firstElement();
			if (prg != null)
				prg.reportDBIOTime(lDbTimeIO_ns);
		}
		catch (NoSuchElementException e)
		{
		}
	}

	public void setSessionPub2000Info(BaseSession session, String csProfitCenter, String csUserId)
	{
		currentUserInfo.csPub2000ProfitCenter = csProfitCenter;
		currentUserInfo.csPub2000UserId = csUserId;
		
		if(session != null)
		{
			session.fillCurrentUserInfo(currentUserInfo);
			csSessionType = session.getType();
			nNetwork_ms = session.getNetwork_ms();
		}
		else
		{
			csSessionType = "Batch";
		}
		isfilled = true;
	}
	
	public boolean isFilled()
	{
		return isfilled;
	}
	
	private int nNbSelect = 0;
	private int nNbInsert = 0;
	private int nNbUpdate = 0;
	private int nNbDelete = 0;
	private int nNbFetchCursor = 0;
	private int nNbCursorOpen = 0;
	private long lDbTimeIO_ns = 0;	// Time in nano seconds
	private long sumDbTimeIO_ns = 0;
	private StopWatchNano swnDbTimeIO = new StopWatchNano();
	private StopWatchNano swnDbTimeRunTransaction = new StopWatchNano();
		
	private String csMachineId = "";
	private String csTomcatId = "";
	
	private String csSessionType = "";
	private String csTerminalId = "";
	
  	//int nTransactionId = 0;
	private int nUniqueSessionRequestId = 0;
	private String csCurrentTransaction = "";
  	
	private CurrentUserInfo currentUserInfo = new CurrentUserInfo();
	
	private AccountingRessourceDesc accountingRessourceDesc = null;
	private Stack<AccountingRecordProgram> accountingStack = new Stack<AccountingRecordProgram>() ;
	private int nTransactionId = 0;
	private boolean isfilled = false;
	
	private int nNetwork_ms = 0;
}
