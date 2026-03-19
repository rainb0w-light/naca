/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.CInitialValue;
import nacaLib.varEx.CobolConstant;
import nacaLib.varEx.DeclareTypeBase;
import nacaLib.varEx.VarDefBuffer;
import nacaLib.varEx.VarDefFPacRaw;
import nacaLib.varEx.VarLevel;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DeclareTypeFPacRaw.java,v 1.2 2006/05/22 11:44:26 u930cv Exp $
 */
public class DeclareTypeFPacRaw extends DeclareTypeBase
{
	public DeclareTypeFPacRaw(VarLevel varLevel, int nLength)
	{
		super(varLevel);
		this.nLength = nLength;
	}

	public int getLength()
	{
		return nLength;
	}
	
	private int nLength = 0;
	
	
	public VarFPacRaw var()
	{
		VarFPacRaw var = new VarFPacRaw(this);
		return var;
	}
	
	public VarFPacRaw filler()
	{
		VarFPacRaw var = new VarFPacRaw(this);
		var.declareAsFiller();
		//return null;
		return var;
	}
	
	public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
	{
		VarDefBuffer varDef = new VarDefFPacRaw(varDefParent, this);
		return varDef;		
	}
		
	/**
	 * 
	 */

	public DeclareTypeFPacRaw value(String cs)
	{
		initialValue = new CInitialValue(cs, false);
		return this;
	}
	
	public DeclareTypeFPacRaw valueAll(char c)
	{
		initialValue = new CInitialValue(c, true);
		return this;
	}

	public DeclareTypeFPacRaw valueAll(String cs)
	{
		initialValue = new CInitialValue(cs, true);
		return this;
	}
	
	public DeclareTypeFPacRaw valueSpaces()
	{
		initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
		return this;
	}

	public DeclareTypeFPacRaw valueZero()
	{
		initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
		return this;
	}

	public DeclareTypeFPacRaw valueHighValue()
	{
		initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
		return this;
	}

	public DeclareTypeFPacRaw valueLowValue()
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
