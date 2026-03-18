/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import nacaLib.varEx.VarDefBase;

public class OccursOverflowException extends NacaRTException
{
	private static final long serialVersionUID = 1L;
	private String csVarDefBase = null;
	private int nIndexRequestedBase1 = 0;
	private int nIndexMaxValueBase1 = 0;
	private String csIndexName = null;
	
	public OccursOverflowException(VarDefBase varDefBase, int nIndexRequestedBase0, int nIndexMaxValue, String csIndexName)
	{
		csVarDefBase = varDefBase.toString();
		nIndexRequestedBase1 = nIndexRequestedBase0+1;
		nIndexMaxValueBase1 = nIndexMaxValue;
		csIndexName = csIndexName;
	}
	
	public String getMessage()
	{
		String cs = "OccursOverflowException: Index " + csIndexName + " value requested/Max:" + nIndexRequestedBase1 + "/" + nIndexMaxValueBase1 + "; VarDef:" + csVarDefBase; // + "; Stack="+csStack;
		return cs;
	}
}
