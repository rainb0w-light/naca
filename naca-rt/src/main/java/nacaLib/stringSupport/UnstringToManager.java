/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.stringSupport;

import nacaLib.varEx.Var;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class UnstringToManager
{
	private UnstringManager unstringManager = null;
	
	UnstringToManager(UnstringManager unstringManager)
	{
		this.unstringManager = unstringManager;
	}
	
	public UnstringToManager to(Var varDelimiterDest, Var varDelimiterIn, Var varCountDest)
	{
		unstringManager.doInto(varDelimiterDest, varDelimiterIn, varCountDest);
		return this;
	}
	
	public UnstringToManager to(Var varDelimiterDest)
	{
		unstringManager.doInto(varDelimiterDest, null, null);
		return this;
	}
	
	public boolean failed()
	{
		if(unstringManager == null)
			return true;
		return unstringManager.failed();
	}
	
	public boolean notFailed()
	{
		if(unstringManager == null)
			return false;
		return !unstringManager.failed();
	}
}
