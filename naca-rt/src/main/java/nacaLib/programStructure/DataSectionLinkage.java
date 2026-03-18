/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 26 août 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.programStructure;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.varEx.DataSection;
import nacaLib.varEx.DataSectionType;

public class DataSectionLinkage extends DataSection
{
	public DataSectionLinkage(BaseProgram prg)
	{
		super(prg, DataSectionType.Linkage);
	}
	
//	public void registerVar(Var var)
//	{
//		int nLevel = var.varManager.getLevel();
//		if(nLevel == 1 || nLevel == 77)
//		{
//			arrMappableVars.add(var);
//		} 
//	}
	
//	public void unregisterVar(Var var)
//	{
//		arrMappableVars.remove(var);
//	}
	
//	private ArrayList arrMappableVars = new ArrayList();	// Array of all var that can be mapped on call 
}
