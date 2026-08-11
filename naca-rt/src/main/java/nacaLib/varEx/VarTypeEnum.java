/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author u930di
 *
 */
package nacaLib.varEx;

public class VarTypeEnum
{
	public static final VarTypeEnum TypeNone = new VarTypeEnum(' ');
	public static final VarTypeEnum TypeX = new VarTypeEnum('X');
	public static final VarTypeEnum Type9 = new VarTypeEnum('9');
	public static final VarTypeEnum TypeGroup = new VarTypeEnum('G');
	public static final VarTypeEnum TypeEditedNum = new VarTypeEnum('N');
	public static final VarTypeEnum TypeEditedAlphaNum = new VarTypeEnum('A');
	public static final VarTypeEnum TypeFieldEdit = new VarTypeEnum('E');

	VarTypeEnum(char c)
	{
		this.c = c;
	}

	char c = 'X';
}
