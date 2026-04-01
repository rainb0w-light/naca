/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.util.ArrayList;

public class GenericToArray<T>
{
	public GenericToArray(ArrayList<T> arrList)
	{
		this.list = arrList;
	}

	public boolean toArray(T[] arr)
	{
		if(list != null && arr != null)
		{			
			int nNbItems = list.size();
			
			for(int n=0; n<nNbItems; n++)
			{
				arr[n] = list.get(n);
			}
			return true;
		}
		return false;
	}
	
	private ArrayList<T> list = null;
}

// sample call to convert a ArrayList<MBeanOperationInfo> to a MBeanOperationInfo[] 
//			ArrayList<MBeanOperationInfo> arrMBeanOperationInfos = ...
//			GenericToArray<MBeanOperationInfo> g = new GenericToArray<MBeanOperationInfo>(arrMBeanOperationInfos);
//			arrOperations = new MBeanOperationInfo[arrMBeanOperationInfosWrapper.size()];
//			g.toArray(arrOperations);

