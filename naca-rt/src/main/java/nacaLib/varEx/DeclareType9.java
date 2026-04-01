/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 11 nov. 04
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
package nacaLib.varEx;

import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;


public class DeclareType9 extends DeclareTypeBase
{
	protected NumericValue numericValue = new NumericValue(); 
	private CInitialValue initialValue = null;
	boolean isblankWhenZero = false;
	
	public DeclareType9()
	{
	}
	
	public void set(VarLevel varLevel, boolean bSigned, int nNbDigitInteger, int nNbDigitDecimal)
	{
		super.set(varLevel);
		numericValue.set(bSigned, nNbDigitInteger, nNbDigitDecimal);
		this.initialValue = null;
		this.isblankWhenZero = false;
	}
	
	public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
	{
		VarDefBuffer varDef = numericValue.createVarDef(varDefParent, this);
		return varDef;		
	}
	
	public VarNum var()
	{
		VarNum var = numericValue.createVar(this);
		return var;
	}

	public VarNum filler()
	{
		VarNum var = numericValue.createVar(this);
		var.declareAsFiller();
		return null;
	}	
	
	public DeclareType9 signLeadingSeparated()
	{
		numericValue.setSignLeadingSeparated(true);
		return this;
	}

	public DeclareType9 signTrailingSeparated()
	{
		numericValue.setSignLeadingSeparated(false);
		return this;
	}		
	
	public DeclareType9 comp3()
	{
		numericValue.nComp = -3;
		return this;		
	}

	public DeclareType9 comp()
	{
		numericValue.nComp = -4;
		return this;		
	}
	
	public DeclareType9 value(double d)
	{
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(d, false);
		return this;
	}
	
	public DeclareType9 value(String s)
	{
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(s, false);
		return this;
	}
		
	public DeclareType9 value(int n)
	{
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(n, false);
		return this;
	}

	public DeclareType9 valueSpaces()
	{
		//initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
		if(getProgramManager().isFirstInstance())
			initialValue = CInitialValueStd.Spaces;
		return this;
	}

	//	
//	// private VarLevelManager varLevelManager = null;
//
//
	public DeclareType9 valueZero()
	{
		//initialValue = new CInitialValue(CobolConstant.Zero.getValue(), false);
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(0, false);
		return this ;
	}

	public DeclareType9 sync()
	{
		return this;
	}
	
	public CInitialValue getInitialValue()
	{
		return initialValue;
	}
	
	

	public DeclareType9 blankWhenZero()
	{
		isblankWhenZero = true;
		return this;
	}
	
	/**
	 * @return
	 */
	public Edit edit()
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		DeclareTypeEditInMapRedefineNum declareTypeEditInMapRedefineNum = tempCache.getDeclareTypeEditInMapRedefineNum();
		declareTypeEditInMapRedefineNum.set(getLevel(), numericValue);
		
		EditInMapRedefineNum var2Edit = new EditInMapRedefineNum(declareTypeEditInMapRedefineNum);
		return var2Edit;
	}
}