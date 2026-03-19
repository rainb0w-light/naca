/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import nacaLib.bdb.BtreeSegmentKeyTypeFactory;

public class SortKeySegmentDefinition
{
	SortKeySegmentDefinition(Var var, boolean bAscending)
	{
		this.var = var;
		this.bAscending = bAscending;
	}
	
	public int getBufferStartPosKey()
	{
		int n = var.getOffsetFromLevel01();
		return n;
	}
	
	public int getBufferLengthKey()
	{
		return var.getLength();
	}
	
	public BtreeSegmentKeyTypeFactory getSegmentKeyType()
	{
		return var.getVarDef().getSegmentKeyTypeFactory();
	}
	
	public Var var = null;
	public boolean bAscending = true;
}
