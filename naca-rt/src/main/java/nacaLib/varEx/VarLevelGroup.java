/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 19 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VarLevelGroup
{
	VarLevelGroup(VarLevel varLevel)
	{
		this.varLevel = varLevel;
	}
	
	public VarGroup var()	// Creates a group
	{
		if(varLevel != null)
			return varLevel.var();
		return null;
	}
	
	public Var filler()
	{
		if(varLevel != null)
			return varLevel.filler();
		return null;
	}
	
	private VarLevel varLevel = null;
}
