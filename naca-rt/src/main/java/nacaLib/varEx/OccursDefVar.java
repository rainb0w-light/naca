/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.varEx;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: OccursDefVar.java,v 1.1 2006/04/19 09:53:09 cvsadmin Exp $
 */
public class OccursDefVar extends OccursDefBase
{	
	public OccursDefVar(Var var)
	{
		var = var;
	}
		
	public int getNbOccurs()
	{
		if(var != null)
			return var.getInt();
		return -1; 
	}
	
	public void prepareAutoRemoval()
	{
		var = null;
	}
	
	public Var getRecordDependingVar()
	{
		return null;
	}

	Var var = null;
}
