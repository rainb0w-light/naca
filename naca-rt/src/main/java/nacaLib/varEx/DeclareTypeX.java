/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 12 nov. 2004
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


public class DeclareTypeX extends DeclareTypeBase
{		
	private int nLength = 0;
	private boolean isjustifyRight = false;
	private CInitialValue initialValue = null;
	
	public DeclareTypeX()
	{
	}
	
	public void set(VarLevel varLevel, int nLength)
	{
		super.set(varLevel);
		this.nLength = nLength;
		this.isjustifyRight = false;
		this.initialValue = null;
	}
	
	int getLength()
	{
		return nLength;
	}
	
	boolean getJustifyRight()
	{
		return isjustifyRight;
	}
	
	public VarAlphaNum var()
	{
		VarAlphaNum var2X = new VarAlphaNum(this);
		return var2X;
	}
	
	public VarAlphaNum filler()
	{
		VarAlphaNum var2X = new VarAlphaNum(this);
		var2X.declareAsFiller();
		//return null;
		return var2X;
	}
	
	public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
	{
		VarDefBuffer varDef = new VarDefX(varDefParent, this);
		return varDef;		
	}
		
	/**
	 * 
	 */

	public DeclareTypeX value(String cs)
	{
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(cs, false);
		return this;
	}
	
	public DeclareTypeX valueAll(char c)
	{
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(c, true);
		return this;
	}

	public DeclareTypeX valueAll(String cs)
	{
		if(getProgramManager().isFirstInstance())
			initialValue = new CInitialValue(cs, true);
		return this;
	}
	
	public DeclareTypeX valueSpaces()
	{
		//initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
		if(getProgramManager().isFirstInstance())
			initialValue = CInitialValueStd.Spaces;
		return this;
	}

	public DeclareTypeX valueZero()
	{
		//initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
		if(getProgramManager().isFirstInstance())
			initialValue = CInitialValueStd.Zero;
		return this;
	}

	public DeclareTypeX valueHighValue()
	{
		//initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
		if(getProgramManager().isFirstInstance())
			initialValue = CInitialValueStd.HighValue;
		return this;
	}

	public DeclareTypeX valueLowValue()
	{
		//initialValue = new CInitialValue(CobolConstant.LowValue.getValue(), true);
		if(getProgramManager().isFirstInstance())
			initialValue = CInitialValueStd.LowValue;
		return this;
	} 
	
	public CInitialValue getInitialValue()
	{
		return initialValue;
	}
	
	public DeclareTypeX justifyRight()
	{
		isjustifyRight = true;
		return this ;
	}
}
