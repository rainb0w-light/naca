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
		if(nIndexCStrMapped < cStrMapped.size())
		{
			CStr cs = cStrMapped.get(nIndexCStrMapped);
			//cs.set(null, 0, 0);	// Erase previous buffer, as we are mapped
			nIndexCStrMapped++;
			return cs;			
		}
		else
		{
			CStr cs = new CStr();
			cStrMapped.add(cs);
			nIndexCStrMapped = cStrMapped.size();
			return cs;
		}		
	}
	
	public CStr getReusable()
	{
		if(nIndexCStrReusable < cStrReusable.size())
		{
			CStr cs = cStrReusable.get(nIndexCStrReusable);
			nIndexCStrReusable++;
			return cs;			
		}
		else
		{
			CStr cs = new CStr();
			cStrReusable.add(cs);
			nIndexCStrReusable = cStrReusable.size();
			return cs;
		}		
	}
	
	public CStrNumber getNumber()
	{
		if(nIndexCStrNumber < cStrNumber.size())
		{
			CStrNumber csNum = cStrNumber.get(nIndexCStrNumber);
			nIndexCStrNumber++;
			return csNum;			
		}
		else
		{
			CStrNumber csNum = new CStrNumber();
			cStrNumber.add(csNum);
			nIndexCStrNumber = cStrNumber.size();
			return csNum;
		}			
	}
	
	public CStrString getString()
	{
		if(nIndexCStrString < cStrString.size())
		{
			CStrString cs = cStrString.get(nIndexCStrString);
			nIndexCStrString++;
			return cs;			
		}
		else
		{
			CStrString cs = new CStrString();
			cStrString.add(cs);
			nIndexCStrString = cStrString.size();
			return cs;
		}
	}
	
	private ArrayList<CStr> cStrMapped = new ArrayList<CStr>();
	private ArrayList<CStr> cStrReusable = new ArrayList<CStr>();
	private ArrayList<CStrNumber> cStrNumber = new ArrayList<CStrNumber>();
	private ArrayList<CStrString> cStrString = new ArrayList<CStrString>();
	

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
