/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.tempCache;

import java.util.ArrayList;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CStrManager.java,v 1.3 2007/01/09 15:54:48 u930di Exp $
 */
public  class CStrManager
{
	public CStr getMapped()
	{
		if(nIndexCStrMapped < strMapped.size())
		{
			CStr cs = strMapped.get(nIndexCStrMapped);
			//cs.set(null, 0, 0);	// Erase previous buffer, as we are mapped
			nIndexCStrMapped++;
			return cs;			
		}
		else
		{
			CStr cs = new CStr();
			strMapped.add(cs);
			nIndexCStrMapped = strMapped.size();
			return cs;
		}		
	}
	
	public CStr getReusable()
	{
		if(nIndexCStrReusable < strReusable.size())
		{
			CStr cs = strReusable.get(nIndexCStrReusable);
			nIndexCStrReusable++;
			return cs;			
		}
		else
		{
			CStr cs = new CStr();
			strReusable.add(cs);
			nIndexCStrReusable = strReusable.size();
			return cs;
		}		
	}
	
	public CStrNumber getNumber()
	{
		if(nIndexCStrNumber < strNumber.size())
		{
			CStrNumber csNum = strNumber.get(nIndexCStrNumber);
			nIndexCStrNumber++;
			return csNum;			
		}
		else
		{
			CStrNumber csNum = new CStrNumber();
			strNumber.add(csNum);
			nIndexCStrNumber = strNumber.size();
			return csNum;
		}			
	}
	
	public CStrString getString()
	{
		if(nIndexCStrString < strString.size())
		{
			CStrString cs = strString.get(nIndexCStrString);
			nIndexCStrString++;
			return cs;			
		}
		else
		{
			CStrString cs = new CStrString();
			strString.add(cs);
			nIndexCStrString = strString.size();
			return cs;
		}
	}
	
	private ArrayList<CStr> strMapped = new ArrayList<CStr>();
	private ArrayList<CStr> strReusable = new ArrayList<CStr>();
	private ArrayList<CStrNumber> strNumber = new ArrayList<CStrNumber>();
	private ArrayList<CStrString> strString = new ArrayList<CStrString>();
	

	private int nIndexCStrMapped = 0;
	private int nIndexCStrReusable = 0;
	private int nIndexCStrNumber = 0;
	private int nIndexCStrString = 0;
	
	public void reset()
	{
		nIndexCStrMapped = 0;
		nIndexCStrReusable = 0;
		nIndexCStrNumber = 0;
		nIndexCStrString = 0;
	}
	
	public void rewindCStrMapped(int n)
	{
		nIndexCStrMapped -= n;
	}
}
