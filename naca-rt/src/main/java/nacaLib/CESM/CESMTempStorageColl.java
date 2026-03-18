/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 3 févr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.CESM;

import java.util.ArrayList;

import nacaLib.varEx.InternalCharBuffer;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CESMTempStorageColl
{
	CESMTempStorageColl()
	{
		arr = new ArrayList<InternalCharBuffer>();
	}
	
	int add(InternalCharBuffer data)
	{
		arr.add(data);
		return arr.size(); 
	}
	
	boolean set(int item, InternalCharBuffer bufItem)
	{
		if (item >=0 && item < arr.size())
		{
			arr.set(item, bufItem);
			return true;
		}
		return false;
	}

	InternalCharBuffer getNextItem()
	{
		if(nLastItemRead+1 < arr.size())
		{
			nLastItemRead++;
			InternalCharBuffer item = arr.get(nLastItemRead);
			return item;
		}
		return null;
	}
	
	InternalCharBuffer getIndexedTempQueue(int nIndex)
	{
		nLastItemRead = nIndex-1;
		if(nLastItemRead>=0 && nLastItemRead < arr.size())
		{
			InternalCharBuffer item = arr.get(nLastItemRead);
			return item;
		}
		return null;
	}
	
	int getNbItems()
	{
		return arr.size();
	}
	
	
	
	
	private ArrayList<InternalCharBuffer> arr = null;
	private int nLastItemRead = -1;
}
