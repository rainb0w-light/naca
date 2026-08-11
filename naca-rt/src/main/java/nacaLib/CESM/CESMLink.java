/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author sly
 *
 */
package nacaLib.CESM;


import jlib.log.Log;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.misc.CCommarea;
import nacaLib.varEx.Var;

public class CESMLink extends CJMapObject
{
	protected BaseEnvironment environment = null ;
	protected String csProgramClassName = null;

	public CESMLink(BaseEnvironment env, String csProgramClassName)
	{
		environment = env;
		this.csProgramClassName = csProgramClassName;
	}

	public void go()
	{
		if(isLogCESM)
			Log.logDebug("Linking program: "+csProgramClassName);
		BaseProgramLoader baseProgramLoader = BaseProgramLoader.GetProgramLoaderInstance();
		baseProgramLoader.runSubProgram(csProgramClassName, null, environment);
	}

	public void commarea(Var var, int length)
	{
		if(isLogCESM)
			Log.logDebug("Linking program: "+csProgramClassName);
		BaseProgramLoader baseProgramLoader = BaseProgramLoader.GetProgramLoaderInstance();
		CCommarea comm = new CCommarea() ;
		environment.setCommarea(comm);
		comm.setVarPassedByRef(var);
		baseProgramLoader.runSubProgram(csProgramClassName, null, environment);
	}

	public void commarea(Var var)
	{
		commarea(var, -1) ;
	}

	public void commarea(Var v, Var length)
	{
		int l = length.getInt() ;
		commarea(v, l) ;
	}

	public void commarea(Var var, int length, int datalength)
	{
		commarea(var, length) ;
	}

	public void commarea(Var var, Var length, int datalength)
	{
		int l = length.getInt() ;
		commarea(var, l) ;
	}
}
