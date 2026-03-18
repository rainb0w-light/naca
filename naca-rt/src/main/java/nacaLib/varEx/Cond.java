/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 1 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author PJD
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.varEx;

import java.util.ArrayList;
import nacaLib.base.CJMapObject;

public class Cond extends CJMapObject 
{
	public Cond(Var varParent, DeclareTypeCond declareTypeCond)
	{
		var = varParent;
		arrValues = declareTypeCond.arrValues;
	}
	
	public String getSTCheckValue()
	{
		return toString();
	}

	private Cond(Var varParent, Cond condValue)
	{
		var = varParent;
		arrValues = condValue.arrValues;	
	}

	public void setTrue()
	{
		int nNbValues = arrValues.size();
		if(nNbValues > 0)
		{
			CondValue condValue = (CondValue)arrValues.get(0);
			String s = condValue.getMin();
			if(s != null)
				var.set(s);
		}
	}
	public boolean is()
	{
		int nNbValues = arrValues.size();
		for(int n=0; n<nNbValues; n++)
		{			
			CondValue condValue = (CondValue)arrValues.get(n);
			if(condValue.is(var))
				return true;
		}
		return false;		
	}
	
	public Cond getAt(Var x_Cmaj)
	{
		return getAt(x_Cmaj.getInt());
	}
	
	public Cond getAt(int x_Cmaj)	// 1 based
	{
		Var var = this.var.getAt(x_Cmaj);
		return new Cond(var, this);
	}
	
	public Cond getAt(VarAndEdit x, VarAndEdit y)
	{
		return getAt(x.getInt(), y.getInt());
	}
	
	public Cond getAt(VarAndEdit x, int y)
	{
		return getAt(x.getInt(), y);
	}
	
	public Cond getAt(int x, VarAndEdit y)
	{
		return getAt(x, y.getInt());
	}
	
	public Cond getAt(int x, int y)
	{
		return new Cond(var.getAt(x, y), this);
	}
	
	public void setName(String csName)
	{
		csName = csName;
	}
	
	public String toString()
	{
		String cs = "Cond {";
		for(int n=0; n<arrValues.size(); n++)
		{
			if(n != 0)
				cs += "; ";
			CondValue condValue = (CondValue)arrValues.get(n);
			cs += condValue.toString();
		}
		cs += "}";
		return cs;
	}

	@SuppressWarnings("unused")
	private String csName = null;
	private Var var = null;
	private ArrayList<CondValue> arrValues = null;	// Array of CondValue
}
