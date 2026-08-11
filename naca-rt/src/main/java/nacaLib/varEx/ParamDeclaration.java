/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.varEx;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgram;

public class ParamDeclaration extends CJMapObject
{
	public ParamDeclaration(BaseProgram Program)
	{
		program = Program;
	}

	public ParamDeclaration using(Var var)
	{
		program.getProgramManager().using(var);
		return this;
	}

	public BaseProgram getProgram()
	{
		return program;
	}

	protected BaseProgram program = null;
}
