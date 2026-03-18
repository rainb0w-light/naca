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
		if(nIndexCStrMapped < arrCStrMapped.size())
		{
			CStr cs = arrCStrMapped.get(nIndexCStrMapped);
			//cs.set(null, 0, 0);	// Erase previous buffer, as we are mapped
			nIndexCStrMapped++;
			return cs;			
		}
		else
		{
			CStr cs = new CStr();
			arrCStrMapped.add(cs);
			nIndexCStrMapped = arrCStrMapped.size();
			return cs;
		}		
	}
	
	public CStr getReusable()
	{
		if(nIndexCStrReusable < arrCStrReusable.size())
		{
			CStr cs = arrCStrReusable.get(nIndexCStrReusable);
			nIndexCStrReusable++;
			return cs;			
		}
		else
		{
			CStr cs = new CStr();
			arrCStrReusable.add(cs);
			nIndexCStrReusable = arrCStrReusable.size();
			return cs;
		}		
	}
	
	public CStrNumber getNumber()
	{
		if(nIndexCStrNumber < arrCStrNumber.size())
		{
			CStrNumber csNum = arrCStrNumber.get(nIndexCStrNumber);
			nIndexCStrNumber++;
			return csNum;			
		}
		else
		{
			CStrNumber csNum = new CStrNumber();
			arrCStrNumber.add(csNum);
			nIndexCStrNumber = arrCStrNumber.size();
			return csNum;
		}			
	}
	
	public CStrString getString()
	{
		if(nIndexCStrString < arrCStrString.size())
		{
			CStrString cs = arrCStrString.get(nIndexCStrString);
			nIndexCStrString++;
			return cs;			
		}
		else
		{
			CStrString cs = new CStrString();
			arrCStrString.add(cs);
			nIndexCStrString = arrCStrString.size();
			return cs;
		}
	}
	
	private ArrayList<CStr> arrCStrMapped = new ArrayList<CStr>();
	private ArrayList<CStr> arrCStrReusable = new ArrayList<CStr>();
	private ArrayList<CStrNumber> arrCStrNumber = new ArrayList<CStrNumber>();	
	private ArrayList<CStrString> arrCStrString = new ArrayList<CStrString>();
	

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
