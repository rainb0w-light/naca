/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: VarBinary.java,v 1.1 2008/04/01 07:23:36 u930di Exp $
 */
public class VarBinary
{
	public VarBinary()
	{
		tb = null;
	}
	
	public VarBinary(byte tb[])
	{
		tb = tb;
	}
	
	public String getAsString()
	{
		if(tb	!= null)
		{
			String cs = new String(tb);
			return cs; 
		}
		
		return "";
	}
	
	public String getAsUTF8String()
	{
		if(tb	!= null)
		{
			String cs;
			try
			{
				cs = new String(tb, "UTF-8");
				return cs; 
			}
			catch (UnsupportedEncodingException e)
			{
				
			}			
		}
		
		return "";
	}
	
	public boolean setUTF8FromString(String cs)
	{
		try
		{
			tb = cs.getBytes("UTF-8");
			return true;
		}
		catch (UnsupportedEncodingException e)
		{
		}
		return false;
	}
	
	public byte [] getBytes()
	{
		return tb;
	}
	
	public void setBytes(byte [] tb)
	{
		tb = tb;
	}
	
	private byte tb[] = null;
}
