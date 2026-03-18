/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.CInitialValue;
import nacaLib.varEx.CobolConstant;
import nacaLib.varEx.DeclareTypeBase;
import nacaLib.varEx.VarDefBuffer;
import nacaLib.varEx.VarDefFPacAlphaNum;
import nacaLib.varEx.VarLevel;

public class DeclareTypeFPacAlphaNum extends DeclareTypeBase 
{
	public DeclareTypeFPacAlphaNum(VarLevel varLevel, int nLength)
	{
		super(varLevel);
		nLength = nLength;
	}

	public int getLength()
	{
		return nLength;
	}
	
	private int nLength = 0;
	
	
	public VarFPacAlphaNum var()
	{
		VarFPacAlphaNum var = new VarFPacAlphaNum(this);
		return var;
	}
	
	public VarFPacAlphaNum filler()
	{
		VarFPacAlphaNum var = new VarFPacAlphaNum(this);
		var.declareAsFiller();
		//return null;
		return var;
	}
	
	public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
	{
		VarDefBuffer varDef = new VarDefFPacAlphaNum(varDefParent, this);
		return varDef;		
	}
		
	/**
	 * 
	 */

	public DeclareTypeFPacAlphaNum value(String cs)
	{
		initialValue = new CInitialValue(cs, false);
		return this;
	}
	
	public DeclareTypeFPacAlphaNum valueAll(char c)
	{
		initialValue = new CInitialValue(c, true);
		return this;
	}

	public DeclareTypeFPacAlphaNum valueAll(String cs)
	{
		initialValue = new CInitialValue(cs, true);
		return this;
	}
	
	public DeclareTypeFPacAlphaNum valueSpaces()
	{
		initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
		return this;
	}

	public DeclareTypeFPacAlphaNum valueZero()
	{
		initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
		return this;
	}

	public DeclareTypeFPacAlphaNum valueHighValue()
	{
		initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
		return this;
	}

	public DeclareTypeFPacAlphaNum valueLowValue()
	{
		initialValue = new CInitialValue(CobolConstant.LowValue.getValue(), true);
		return this;
	} 
	
	public CInitialValue getInitialValue()
	{
		return initialValue;
	}
	
	private CInitialValue initialValue = null;
}

