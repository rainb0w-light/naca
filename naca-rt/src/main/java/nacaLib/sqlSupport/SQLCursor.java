/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 15 févr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.sqlSupport;

import jlib.log.Log;
import jlib.log.StackStraceSupport;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.varEx.VarAndEdit;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SQLCursor  extends CJMapObject // extends SQLCursor 
{
	public SQLCursor(BaseProgramManager programManager)
	{
		programManager = programManager;
		sQL = null;
		bOpen = false;
		programManager.registerCursor(this);
	}
	
	public void setQuery(String csQuery)
	{
		//VarBuffer working = programManager.dataDivision.getWorkingStorageSectionVarBuffer();
		//CESMEnvironment env = programManager.cESMEnv;
		//CSQLStatus sqlstatus = programManager.getSQLStatus();
		//sQL = new SQL(working, env, csQuery, true, sqlstatus);
		
		//String csFileLine = StackStraceSupport.getFileLineAtStackDepth(3);	// Caller File Line
		//sQL = programManager.getOrCreateSQLForCursor(csQuery, this);//, csFileLine);
		sQL = programManager.getOrCreateSQLGeneral(csQuery, this);//, csFileLine);
	}
	
	public void setMustBeNamed(boolean bNameToSet)
	{
		bNameToSet = bNameToSet;
	}
	
	public boolean getMustNameCursor()
	{
		return bNameToSet;
	}
	
//	public SQLCursor(ProgramManager programManager, VarBuffer Working, CESMEnvironment env, String csQuery, CSQLStatus sqlstatus)
//	{
//		programManager = programManager;
//		sQL = new SQL(Working, env, csQuery, true, sqlstatus);
//		bOpen = false;
//	}
	
	public CSQLStatus open()
	{
		CSQLStatus sqlStatus = programManager.getSQLStatus();
		if(sQL != null)
		{
			if(bOpen)
				sqlStatus.setSQLCode(SQLCode.SQL_CURSOR_ALREADY_OPENED);
			else
				sqlStatus.reset();
		}		
		bOpen = true;
		return sqlStatus;
	}
	
	public CSQLStatus close()
	{
		CSQLStatus sqlStatus = programManager.getSQLStatus();
		if(sQL != null)
		{
			if(!bOpen)
				sqlStatus.setSQLCode(SQLCode.SQL_CURSOR_NOT_OPEN);				
			else
				sqlStatus.reset();
			sQL.close();			
		}
		else	// too many close
		{
			sqlStatus.setSQLCode(SQLCode.SQL_CURSOR_NOT_OPEN);
		}
		bOpen = false;

		sQL = null;
		sQLCursorFetch = null;
		return sqlStatus;
	}
	
	public void closeIfOpen()
	{
		if(bOpen)
		{
			close();
		}
	}
		
	public boolean isOpen()
	{
		return bOpen;
	}
	
	public SQLCursor param(int nName, VarAndEdit var)
	{
		if(isLogSql)
			Log.logDebug("param "+nName+"="+var.getLoggableValue());
		if(sQL != null)
			sQL.param(nName, var);
		return this;
	}
	
	public SQLCursor param(String csName, VarAndEdit var)
	{
		if(isLogSql)
			Log.logDebug("param "+csName+"="+var.getLoggableValue());
		if(sQL != null)
			sQL.param(csName, var);
		return this;
	}
	
	public SQLCursor param(int nName, int nValue)
	{
		if(isLogSql)
			Log.logDebug("param "+nName+"="+nValue);
		sQL.param(nName, nValue);
		return this;
	}
	
	public SQLCursor param(String csName, int nValue)
	{
		if(isLogSql)
			Log.logDebug("param "+csName+"="+nValue);
		if(sQL != null)
			sQL.param(csName, nValue);
		return this;
	}

	public SQLCursor param(int nName, double dValue)
	{
		if(isLogSql)
			Log.logDebug("param "+nName+"="+dValue);
		if(sQL != null)
			sQL.param(nName, dValue);
		return this;
	}
	
	public SQLCursor param(String csName, double dValue)
	{
		if(isLogSql)
			Log.logDebug("param "+csName+"="+dValue);
		if(sQL != null)
			sQL.param(csName, dValue);
		return this;
	}
		
	public SQLCursor param(int nName, String csValue)
	{
		if(isLogSql)
			Log.logDebug("param "+nName+"="+csValue);
		if(sQL != null)
			sQL.param(nName, csValue);
		return this;
	}
	
	public SQLCursor param(String csName, String csValue)
	{	
		if(isLogSql)
			Log.logDebug("param "+csName+"="+csValue);
		if(sQL != null)
			sQL.param(csName, csValue);
		return this;
	}
	
	public SQLCursor onWarningGoto(Paragraph paragraphSQGErrorGoto)
	{
		if(isLogSql)
			Log.logDebug("onWarningGoto "+paragraphSQGErrorGoto.toString());
		if(sQL != null)
			sQL.onWarningGoto(paragraphSQGErrorGoto);
		return this;
	}
	
	public SQLCursor onWarningGoto(Section section)
	{
		if(isLogSql)
			Log.logDebug("onWarningGoto "+section.toString());
		if(sQL != null)
			sQL.onWarningGoto(section);
		return this;
	}
	
	public SQLCursor onWarningContinue()
	{
		if(isLogSql)
			Log.logDebug("onWarningContinue");
		if(sQL != null)
			sQL.onWarningContinue();
		return this;
	}
	
	public SQLCursor onErrorGoto(Paragraph paragraphSQGErrorGoto)
	{
		if(isLogSql)
			Log.logDebug("onErrorGoto "+paragraphSQGErrorGoto.toString());
		if(sQL != null)
			sQL.onErrorGoto(paragraphSQGErrorGoto);
		return this;
	}
	
	public SQLCursor onErrorGoto(Section section)
	{
		if(isLogSql)
			Log.logDebug("onErrorGoto "+section.toString());
		if(sQL != null)
			sQL.onErrorGoto(section);
		return this;
	}
	
	public SQLCursor onErrorContinue()
	{
		if(isLogSql)
			Log.logDebug("onErrorContinue");
		if(sQL != null)
			sQL.onErrorContinue();
		return this;
	}
	
	public SQLCursorFetch fetch(BaseEnvironment env)
	{
		if(sQLCursorFetch == null)
			sQLCursorFetch = new SQLCursorFetch(bOpen, sQL);
		if(bOpen && sQL != null)
		{
			sQL.resetExecuted(env);
			sQL.resetErrorManager();
			// PJD ROWID Support:
			//	if(sQL.hasRowIdGenerated())
			//{
			//	sqlItemRowId = new CSQLIntoItem();	
			//	sQL.into(sqlItemRowId);
			//}			
		}
		return sQLCursorFetch;		
	}
	
	public void setName(String csProgramName, String csName)
	{
		String cs = csProgramName + csName;
		csUniqueName = cs.toUpperCase();
	}

	public String getUniqueCursorName()	// use for updatable cusrot that use Cursor Name
	{
		return csUniqueName;
	}
	
	private SQLCursorFetch sQLCursorFetch = null; 
	public /*private*/ SQL sQL = null;
	private boolean bOpen = false;
	private BaseProgramManager programManager = null;
	private String csUniqueName = null;
	private boolean bNameToSet = false;	
	
	public SQLCursor setHoldability(boolean b)
	{
		sQL.setHoldability(b);
		return this;
	}
}
