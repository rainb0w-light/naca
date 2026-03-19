/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 22 f�vr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.sqlSupport;

import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SQLCursorFetch
{
//	public SQLCursorFetch(VarBufferPos Working, CSQLConnection SQLConnection, String csQuery)
//	{
//	}	
	public SQLCursorFetch(boolean bOpen, SQL sql)
	{
		this.bOpen = bOpen;
		this.sQL = sql;
	}
	
//	public SQLCursorFetch fetch()
//	{
//		//if(bOpen && sQL != null)
//		{
//			// PJD ROWID Support:
//			/*
//			if(sQL.hasRowIdGenerated())
//			{
//				sqlItemRowId = new CSQLIntoItem();	
//				sQL.into(sqlItemRowId);
//			}
//			*/
//		}
//		return this;
//	}


	public SQLCursorFetch into(VarAndEdit varInto)
	{
		if(bOpen && sQL != null)
		{
			sQL.into(varInto, null);
		}	
		return this;
	}
	
	public SQLCursorFetch into(Var varInto, Var varIndicator)
	{
		if(bOpen && sQL != null)
			sQL.into(varInto, varIndicator);
		return this;
	}
	
//	public String getCursorName()	// use for updatable cusrot that use Cursor Name
//	{
//		if(sQL != null)
//			return sQL.getCursorName();
//		return null;
//	}
	
	public SQLCursorFetch onErrorGoto(Paragraph paragraphSQGErrorGoto)
	{
		sQL.onErrorGoto(paragraphSQGErrorGoto);
		return this;
	}
	
	public SQLCursorFetch onErrorGoto(Section section)
	{
		sQL.onErrorGoto(section);
		return this;
	}
	
	public SQLCursorFetch onErrorContinue()
	{
		sQL.onErrorContinue();
		return this;
	}
	public SQLCursorFetch onWarningGoto(Paragraph paragraphSQGErrorGoto)
	{
		// TODO
		return this;
	}
	
	public SQLCursorFetch onWarningGoto(Section section)
	{
		// TODO
		return this;
	}
	
	public SQLCursorFetch onWarningContinue()
	{
		// TODO
		return this;
	}
	
	
	// PJD ROWID Support:
	/*
	public ROWID getCurrentRowId()
	{
		if(sqlItemRowId != null)
			return sqlItemRowId.getRowId();
		return null;
	}
	*/
	
	// PJD ROWID Support:private CSQLIntoItem sqlItemRowId = null;	// Used for updatable cursor that use RowId
	//protected SQL sQL = null;
	private boolean bOpen = false;
	public /*must be private */SQL sQL = null;
}
