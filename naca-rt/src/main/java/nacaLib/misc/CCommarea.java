/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 4 oct. 04
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
package nacaLib.misc;

import nacaLib.varEx.*;
import nacaLib.base.CJMapObject;

public class CCommarea extends CJMapObject
{
	public CCommarea()
	{
	}
	
	public void setVarPassedByValue(Var var, int length)
	{
		if (var.getLength() < length)
		{
			length = var.getLength();
		}
		charBufferCopy = var.exportToCharBuffer(length);
		var = null;
		isbyValue = true;
	}
	public void setVarPassedByValue(InternalCharBuffer buff)
	{
		charBufferCopy = buff ;
		var = null;
		isbyValue = true;
	}

	public void setVarPassedByValue(Form form)
	{
		charBufferCopy = form.encodeToCharBuffer();
		var = null;
		isbyValue = true;
	}

	public void setVarPassedByRef(Var var)
	{
		charBufferCopy = null;
		this.var = var;
		isbyValue = false;
	}

	void setLength(int nLength)
	{
		this.nLength = nLength;
		islengthSpecified = true;
	}
	
	public int getLength()
	{
		if(var != null)
		{
			if(islengthSpecified)
				return nLength;
			return var.getLength(); 
		}
		if(charBufferCopy != null)
		{
			if(islengthSpecified)
				return nLength;
			return charBufferCopy.getBufferSize(); 
		}
		return 0; 
	}
	
	public CCallParam buildCallParam()
	{
		if(var != null)	// By ref
		{
			CallParamByRef callParam = new CallParamByRef(var);
			return callParam;
		}
		if(charBufferCopy != null)	// By value
		{
			CallParamByCharBuffer callParam = new CallParamByCharBuffer(charBufferCopy);
			return callParam;
		}
		return null;
	}
	
	public CallParamFpac buildCallParamFPac()
	{
		if(charBufferCopy != null)	// By value
		{
			CallParamFpac callParam = new CallParamFpac(charBufferCopy);
			return callParam;
		}
		return null;
	}
		
	private boolean isbyValue = false;
	private Var var = null;
	private InternalCharBuffer charBufferCopy = null;
	private int nLength = 0;
	private boolean islengthSpecified = false;
	/**
	 * @param varDest
	 */
//	public CCallParam GetParam()
//	{
//		CallParamByCharBuffer param = new CallParamByCharBuffer(charBufferCopy) ;
//		return param ;
//		//param.MapOn(varDest) ;
//	}
}
