/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.varEx.Var;

public class DumpProgramException extends NacaRTException
{
	private static final long serialVersionUID = 1L;
	private String csProgramName = "";
	private String csVar1 = "";
	private String csVar2 = "";
	
	public DumpProgramException(BaseProgramManager programManager, Var var1, Var var2)
	{
		csProgramName = programManager.getProgramName();
		if (var1 != null)
			csVar1 = var1.toString();
		if (var2 != null)
			csVar2 = var2.toString();
	}
	
	public String getMessage()
	{
		String cs = "DumpProgramException: Program:" + csProgramName + "; Var1:" + csVar1 + "; Var2:" + csVar2; // + " Stack="+csStack;
		return cs;
	}
}
