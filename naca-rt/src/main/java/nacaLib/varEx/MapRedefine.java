/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Creat/d on 15 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;
import nacaLib.tempCache.CStr;

public class MapRedefine extends Var
{
	public MapRedefine(DeclareTypeMapRedefine declareTypeMapRedefine)
	{
		super(declareTypeMapRedefine);
		formRedefineOrigin = declareTypeMapRedefine.formRedefineOrigin;
	}
	
	protected MapRedefine()
	{
		super();
	}
	
	protected VarBase allocCopy()
	{
		MapRedefine v = new MapRedefine();
		return v;
	}

	/* (non-Javadoc)
	 * @see nacaLib.varEx.VarBase#getAsLoggableString()
	 */
	protected String getAsLoggableString()
	{
		//return varDef.getRawStringIncludingHeader(bufferPos);
		CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
		String cs = cstr.getAsString();
		//cstr.resetManagerCache();
		return cs;
	}
	
	public boolean hasType(VarTypeEnum e)
	{
		return false;
	}

	public void encodeToVar(Var varDest)
	{
		varDef.varDefFormRedefineOrigin.encodeToVar(bufferPos, varDest);
	}
	
	public void decodeFromVar(Var varSource)
	{
		varDef.varDefFormRedefineOrigin.decodeFromVar(bufferPos, varSource);
	}
	
	public InternalCharBuffer encodeToCharBuffer()
	{
		int nDestLength = varDef.getBodyLength() + varDef.getHeaderLength();
		VarDefForm varDefFormOrigin = varDef.varDefFormRedefineOrigin;
		return varDefFormOrigin.encodeToCharBuffer(nDestLength);
	}
	
	public void decodeFromCharBuffer(InternalCharBuffer charBufferSource)
	{
		VarDefForm varDefFormOrigin = varDef.varDefFormRedefineOrigin;
		varDefFormOrigin.decodeFromCharBuffer(bufferPos, charBufferSource);
	}
	
	public String getStringIncludingHeader()
	{
		CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
		String cs = cstr.getAsString();
		//cstr.resetManagerCache();
		return cs;
		//return varDef.getRawStringIncludingHeader(bufferPos);
	}
	
	public void initialize()
	{
		if(formRedefineOrigin != null)
		{
			InitializeCache initializeCache = getProgramManager().getOrCreateInitializeCache(getVarDef());
			formRedefineOrigin.initialize(initializeCache);
			// Was before optimizations: formRedefineOrigin.initialize();
		}
	}
	
	public int compareTo(int nValue)
	{
		int nVarValue = getInt();
		return nVarValue - nValue;
	}
	
	
	public int compareTo(double dValue)
	{
		double varValue = getDouble();
		double d = varValue - dValue;
		if(d < -0.00001)	//Consider epsilon precision at 10 e-5 
			return -1;
		else if(d > 0.00001)	//Consider epsilon precision at 10 e-5
			return 1;
		return 0;			
	} 
	

	protected byte[] convertUnicodeToEbcdic(char[] tChars)
	{
		return AsciiEbcdicConverter.noConvertUnicodeToEbcdic(tChars);
	}
	
	protected char[] convertEbcdicToUnicode(byte[] tBytes)
	{
		return AsciiEbcdicConverter.noConvertEbcdicToUnicode(tBytes);
	}
	
	public VarType getVarType()
	{
		return VarType.VarMapRedefine;
	}
	
	Form formRedefineOrigin = null;
}
