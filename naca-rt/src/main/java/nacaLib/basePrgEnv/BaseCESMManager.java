/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;

import jlib.log.Log;
import jlib.misc.DateUtil;
import jlib.misc.Time_ms;
import nacaLib.CESM.CESMLink;
import nacaLib.CESM.CESMQueueManager;
import nacaLib.CESM.CESMReadQueue;
import nacaLib.CESM.CESMReturnCode;
import nacaLib.CESM.CESMStart;
import nacaLib.CESM.CESMWriteQueue;
import nacaLib.CESM.CESMXctl;
import nacaLib.base.CJMapObject;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.exceptions.CESMAbendException;
import nacaLib.exceptions.CESMReturnException;
import nacaLib.misc.CCESMFakeMethodContainer;
import nacaLib.misc.CCommarea;
import nacaLib.misc.Pointer;
import nacaLib.program.CESMCommandCode;
import nacaLib.program.CJMapRunnable;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.varEx.Form;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

public class BaseCESMManager extends CJMapObject
{
	protected BaseEnvironment cESMEnv = null ;
	
	public BaseCESMManager(BaseEnvironment env)
	{
		cESMEnv = env;
	}
	
	public BaseEnvironment getEnvironment()
	{
		return cESMEnv ;
	}
	
	public void returnTrans(String csTransaction, Var v1, VarAndEdit len)
	{
		int l = len.getInt();
		returnTrans(csTransaction, v1, l, true);
	}
	public void returnTrans(Class cl, Var v1, VarAndEdit len)
	{
		returnTrans(cl.getName(), v1, len.getInt(), false);
	}
	public void returnTrans(String csTransaction, Form f1, VarAndEdit len)
	{
		returnTrans(csTransaction, f1, len.getInt(), true);
	}
	public void returnTrans(Class cl, Form f1, VarAndEdit len)
	{
		returnTrans(cl.getName(), f1, len.getInt(), false);
	}
	public void returnTrans(VarAndEdit varTransaction, Var v1, VarAndEdit len)
	{
		returnTrans(varTransaction.getString(), v1, len.getInt(), true);
	}
	public void returnTrans(VarAndEdit varTransaction, Var v1)
	{
		returnTrans(varTransaction.getString(), v1, v1.getLength(), true);
	}
	public void returnTrans(VarAndEdit varTransaction, Form form1, VarAndEdit len)
	{
		returnTrans(varTransaction.getString(), form1, len.getInt(), true);
	}	
	public void returnTrans(Class cl, Var v1)
	{
		returnTrans(cl.getName(), v1, v1.getLength(), false);
	}
	public void returnTrans(String csTransaction, Var v1)
	{
		returnTrans(csTransaction, v1, v1.getLength(), true);
	}
	public void returnTrans(String csTransaction, Var v1, int length)
	{
		returnTrans(csTransaction, v1, length, true);
	}	
	public void returnTrans(Class cl, Form form)
	{
		returnTrans(cl.getName(), form, false);
	}	
	public void returnTrans(String csTransaction, Form form)
	{
		returnTrans(csTransaction, form, true);
	}
	
	public void returnTrans()
	{
		if(isLogCESM)
			Log.logDebug("returnTrans");
		cESMEnv.setLastCommandCode(CESMCommandCode.RETURN);
		cESMEnv.setNextProgramToLoad("");
		cESMEnv.setCommarea(null);
		CESMReturnException excp = new CESMReturnException();
		throw excp;
	}	
	private void returnTrans(String csProgramId, Form form, boolean bResolveProgram)
	{
		if (bResolveProgram)
			csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);
			
		if(isLogCESM)
			Log.logDebug("returnTrans program="+csProgramId+" Form="+form.getLoggableValue());		
		cESMEnv.setLastCommandCode(CESMCommandCode.RETURN);
		cESMEnv.setNextProgramToLoad(csProgramId) ;
		CCommarea comm = new CCommarea() ;
		comm.setVarPassedByValue(form);
		cESMEnv.setCommarea(comm);
		CESMReturnException excp = new CESMReturnException();
		throw excp;
	}	
	private void returnTrans(String csProgramId, Var v1, int length, boolean bResolveProgram)
	{
		if (bResolveProgram)
			csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);
		if (length > v1.getLength())
			length = v1.getLength();
		
		if(isLogCESM)
			Log.logDebug("returnTrans program="+csProgramId+ " Var="+v1.getLoggableValue());		
		cESMEnv.setLastCommandCode(CESMCommandCode.RETURN) ;
		cESMEnv.setNextProgramToLoad(csProgramId) ;
		CCommarea comm = new CCommarea() ;
		comm.setVarPassedByValue(v1, length);
		cESMEnv.setCommarea(comm);
		CESMReturnException excp = new CESMReturnException();
		throw excp;
	}
	
	public void abend()
	{
		if(isLogCESM)
			Log.logDebug("abend");
		cESMEnv.setLastCommandCode(CESMCommandCode.ABEND);
		CESMAbendException e = new CESMAbendException("none");
		throw e;
	}

	public void abend(VarAndEdit v)
	{
		abend(v.getString()); 
	}
	public void abend(String cs)
	{
		if(isLogCESM)
			Log.logDebug("abend");
		cESMEnv.setLastCommandCode(CESMCommandCode.ABEND) ;
		CESMAbendException e = new CESMAbendException(cs);
		throw e ; 
	}

	public BaseCESMManager getAddressOfTCTUA(Pointer p)
	{
		if(isLogCESM)
			Log.logDebug("getAddressOfTCTUA");
		cESMEnv.setLastCommandCode(CESMCommandCode.GET_ADDRESS) ;
		//p.addressOf.varManager.redefinesAs(cESMEnv.getTCTUA());
		
		char [] acTCTUA = cESMEnv.getTCTUA();	
		p.addressOf.setCustomBuffer(acTCTUA);
		
		return this;	
	}

	public BaseCESMManager getAddressOfTWA(Pointer p)
	{
		if(isLogCESM)
			Log.logDebug("getAddressOfTCTUA");
		// p.addressOf.varManager.redefinesAs(cESMEnv.getTWA());
		char [] acTWA = cESMEnv.getTWA();
		p.addressOf.setCustomBuffer(acTWA);
		//p.addressOf.varManager.manageRedefines();
			
		return this;	
	}
	
	public BaseCESMManager getAddressOfCWA(Pointer p)
	{
		if(isLogCESM)
			Log.logDebug("getAddressOfTCTUA");
		//p.addressOf.varManager.redefinesAs(cESMEnv.getCWA());
		char [] acCWA = cESMEnv.getCWA();
		p.addressOf.setCustomBuffer(acCWA);
		
		return this;	
	}

	public CCESMFakeMethodContainer assign()
	{
		// TODO fake method CEMS Assign
		return new CCESMFakeMethodContainer() ;
	}
	
	public BaseCESMManager ignoreCondition(String string)
	{
		if(isLogCESM)
			Log.logDebug("ignoreCondition "+string);
		cESMEnv.setLastCommandCode(CESMCommandCode.IGNORE) ;
		tabConditionHandles.remove(string);
		return this ;
	}
	public BaseCESMManager unhandleCondition(String string)
	{
		if(isLogCESM)
			Log.logDebug("unhandleCondition"+string);
		cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
		tabConditionHandles.remove(string);
		return this ;
	}
	public BaseCESMManager handleCondition(String string, Paragraph par)
	{
		if(isLogCESM)
			Log.logDebug("handleCondition"+string);
		cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
		tabConditionHandles.put(string, par);
		return this ;
	}
	public BaseCESMManager handleCondition(String string, Section par)
	{
		if(isLogCESM)
			Log.logDebug("handleCondition"+string);
		cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
		tabConditionHandles.put(string, par);
		return this ;
	}
	protected Hashtable<String, CJMapRunnable> tabConditionHandles = new Hashtable<String, CJMapRunnable>();
	
	public String getLastCommandReturnCode()
	{
		return cESMEnv.getLastCommandReturnCode().getCode();
	}

	public int getConditionOccured()
	{
		int n = cESMEnv.getLastCommandReturnCode().getCondition() ;
		if(isLogCESM)
			Log.logDebug("getConditionOccured value="+n);
		return n;
	}

	public void setConditionOccured(int n)
	{
		if(isLogCESM)
			Log.logDebug("setConditionOccured value="+n);
		cESMEnv.setCommandReturnCode(CESMReturnCode.Select(n)) ;
	}

	public CCESMFakeMethodContainer startBrowseDataSet(String ws_Fichier)
	{
		// TODO fake Method
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer startBrowseDataSet(Var ws_Fichier)
	{
		// TODO fake Method
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer readNextDataSet(Var res_Fichier)
	{
		// TODO fake Method
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer readNextDataSet(String res_Fichier)
	{
		// TODO fake Method
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer readPreviousDataSet(Var res_Fichier)
	{
		// TODO fake Method
		return new CCESMFakeMethodContainer() ;
	}

	public String getConfig(String string)
	{
		return cESMEnv.getConfigOption(string) ;
	}

	public String getSQLEnvironment()
	{
		if(isLogCESM)
			Log.logDebug("getSQLEnvironment");
		return cESMEnv.getSQLConnection().getEnvironmentPrefix() ;
	}

	public void delayInterval(Var delay)
	{
		// delay uses format HHMMSS
		int nNextTime_s = DateUtil.getNbSecondsFromHour(delay.getInt());
		long lWaitTime_ms = nNextTime_s * 1000;
		cESMEnv.offsetMaxTimeLimit(lWaitTime_ms);
		Time_ms.wait_ms(lWaitTime_ms);
	}
	public void delaySeconds(Var delay)
	{
		long lWaitTime_ms = delay.getLong() * 1000;
		cESMEnv.offsetMaxTimeLimit(lWaitTime_ms);
		Time_ms.wait_ms(lWaitTime_ms);		
	}

	public boolean hasCredentials()
	{
		return !cESMEnv.getApplicationCredentials().equals("");
	}
	public String getDeclaredUserId()
	{
		if (cESMEnv.getApplicationCredentials().length() > 7)
		{
			return cESMEnv.getApplicationCredentials().substring(5, 8);
		}
		else
		{
			return cESMEnv.getApplicationCredentials().substring(5, 7);
		}
	}
	public String getDeclaredCompany()
	{
		return cESMEnv.getApplicationCredentials().substring(0, 2) ;
	}
	public String getDeclaredAgency()
	{
		return cESMEnv.getApplicationCredentials().substring(2, 5) ;
	}	

	public CCESMFakeMethodContainer enQ(Var enqsycr, int i)
	{
		// TODO fake method CEMS ENQ
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer deQ(Var enqsycr, int i)
	{
		// TODO fake method CEMS DEQ
		return new CCESMFakeMethodContainer() ;
	}


	public CCESMFakeMethodContainer setTDQueueClosed(String string)
	{
		// TODO fake method CEMS TD CLOSE
		return new CCESMFakeMethodContainer() ;
	}
	public CCESMFakeMethodContainer setTDQueueOpen(String string)
	{
		// TODO fake method CEMS TD CLOSE
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer writeTransiantQueue(String string)
	{
		// TODO fake method writeTransiantQueue
		return new CCESMFakeMethodContainer() ;
	}

	public CCESMFakeMethodContainer getMain()
	{
		// TODO fake method getMain
		return new CCESMFakeMethodContainer() ;
	}

	public String getCurrentDay()
	{
		Calendar cal = Calendar.getInstance() ;
		int day = cal.get(Calendar.DAY_OF_MONTH) ;
		String cs = "" + (day/10) + (day%10) ;
		return cs ;
	}

	public Calendar getCurrentDate()
	{
		Calendar cal = Calendar.getInstance() ;
		return cal ;
	}

	public String getCurrentMonth()
	{
		Calendar cal = Calendar.getInstance() ;
		int n = cal.get(Calendar.MONTH) +1 ;
		String cs = "" + (n/10) + (n%10) ;
		return cs ;
	}

	public String getCurrentShortYear()
	{
		Calendar cal = Calendar.getInstance() ;
		int n = cal.get(Calendar.YEAR) ;
		String cs = "" + ((n%100)/10) + (n%10) ;
		return cs ;
	}
	
	public void askTime()
	{
		if(isLogCESM)
			Log.logDebug("askTime");
		cESMEnv.setLastCommandCode(CESMCommandCode.ASKTIME) ;
		cESMEnv.resetDateTime() ;
	}
	
	public CCESMFakeMethodContainer inquire()
	{
		if(isLogCESM)
			Log.logDebug("inquire");
		
		cESMEnv.setLastCommandCode(CESMCommandCode.INQUIRE) ;
		// TODO fake method inquire
		return new CCESMFakeMethodContainer();
	}

	public CESMReadQueue readTempQueue(Var varName)
	{
		return readTempQueue(varName.getString());
	}
	public CESMReadQueue readTempQueue(String csName)
	{
		if(isLogCESM)
			Log.logDebug("readTempQueue "+csName);
		
		cESMEnv.setLastCommandCode(CESMCommandCode.READ_TEMPQUEUE);		
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		
		return new CESMReadQueue(false, csName, cESMEnv.getQueueManager());
	}
	
	public void deleteTempQueue(Var varName)
	{
		deleteTempQueue(varName.getString());
	}
	public void deleteTempQueue(String csName)
	{
		if(isLogCESM)
			Log.logDebug("deleteTempQueue "+csName);
		
		cESMEnv.setLastCommandCode(CESMCommandCode.DELETE_TEMPQUEUE);
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		
		CESMQueueManager queueManager = cESMEnv.getQueueManager();
		queueManager.deleteTempQueue(csName);
	}
	
	
	public CESMWriteQueue writeTempQueue(Var varName)
	{
		return writeTempQueue(varName.getString());	
	}
	public CESMWriteQueue writeTempQueue(String csName)
	{
		if(isLogCESM)
			Log.logDebug("writeTempQueue "+csName);
		
		cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_TEMPQUEUE);
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		
		return new CESMWriteQueue(false, csName, cESMEnv.getQueueManager());
	}

	public CESMWriteQueue writeTempQueue(Var tsNom, Var reWriteItem)
	{
		if(isLogCESM)
			Log.logDebug("writeTempQueue "+tsNom.getLoggableValue());
		
		cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_TEMPQUEUE);
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		
		String name = tsNom.getString();
		CESMWriteQueue writeorder = new CESMWriteQueue(false, name, cESMEnv.getQueueManager());
		int item = reWriteItem.getInt() ;
		writeorder.rewrite(item) ;
		return writeorder ;	
	}

	public CCESMFakeMethodContainer readDataSet(Var var)
	{
		return readDataSet(var.getString());
	}
	public CCESMFakeMethodContainer readDataSet(String string)
	{
		if(isLogCESM)
			Log.logDebug("readDataSet "+string);
		cESMEnv.setLastCommandCode(CESMCommandCode.READ_DATASET) ;
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		// --> VSAM will be suppressed from COBOL
		return new CCESMFakeMethodContainer();
	}
	
	public CCESMFakeMethodContainer writeDataSet(Var var)
	{
		return writeDataSet(var.getString());
	}
	public CCESMFakeMethodContainer writeDataSet(String string)
	{
		if(isLogCESM)
			Log.logDebug("writeDataSet "+string);
		cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_DATASET) ;
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		// --> VSAM will be suppressed from COBOL
		return new CCESMFakeMethodContainer();
	}
	
	public CCESMFakeMethodContainer reWriteDataSet(String string)
	{
		if(isLogCESM)
			Log.logDebug("reWriteDataSet "+string);
		cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;
		// --> VSAM will be suppressed from COBOL
		return new CCESMFakeMethodContainer();
	}
	
	public CESMStart start(String csTransaction)
	{
		return start(csTransaction, true);
	}
	public CESMStart start(Var varTransaction)
	{
		return start(varTransaction.getString(), true);
	}
	public CESMStart start(Class cl)
	{
		return start(cl.getName(), false);
	}
	private CESMStart start(String csProgramId, boolean bResolveProgram)
	{
		if (bResolveProgram)
			csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);

		if(isLogCESM)
			Log.logDebug("start "+csProgramId);
		cESMEnv.setCommarea(null);
		return new CESMStart(csProgramId, cESMEnv);
	}

	public void syncPointRollback()
	{
		if(cESMEnv.hasSQLConnection())
		{
			if(isLogCESM)
				Log.logDebug("syncPointRollback");
			cESMEnv.rollbackSQL();
		}
		else
		{
			if(isLogCESM)
				Log.logDebug("syncPointRollback: Nothing to do: No connection opened");
		}
	}
	
	public void syncPointCommit()
	{
		if(cESMEnv.hasSQLConnection())
		{
			if(isLogCESM)
				Log.logDebug("syncPointCommit");
			SQLException e = cESMEnv.commitSQL();
			if(e != null)
			{
				AbortSessionException exp = new AbortSessionException() ;
				exp.reason = new Error("Problem with syncPointCommit");
				exp.programName = null;
				throw exp ;
			}
		}
		else
		{
			if(isLogCESM)
				Log.logDebug("syncPointCommit: Nothing to do: No connection opened");
		}
	}
	
	public CESMLink link(Var varProgram)
	{
		return link(varProgram.getString().trim());
	}
	public CESMLink link(Class cl)
	{
		return link(cl.getName());
	}
	public CESMLink link(String csProgramName)
	{
		if(isLogCESM)
			Log.logDebug("link "+csProgramName);
		cESMEnv.setLastCommandCode(CESMCommandCode.LINK );
		cESMEnv.setCommarea(null);
		return new CESMLink(cESMEnv, csProgramName);
	}
	
	public CESMXctl xctl(Class cl)
	{
		return xctl(cl.getName());
	}
	public CESMXctl xctl(Var varProgram)
	{
		return xctl(varProgram.getString());
	}
	public CESMXctl xctl(String csProgram)
	{
		if(isLogCESM)
			Log.logDebug("xctl "+csProgram);
		cESMEnv.setLastCommandCode(CESMCommandCode.XCTL);
		cESMEnv.setCommarea(null);
		return new CESMXctl(cESMEnv, csProgram);
	}

	public String getLastCommandCode()
	{
		return cESMEnv.getLastCommandCode() ;
	}
}