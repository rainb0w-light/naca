/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.classLoader;

import java.util.ArrayList;

public class CoupleCodeLoader
{
	public CoupleCodeLoader(Class classCode, ClassDynLoader classDynLoader)
	{
		classCode = classCode;
		classDynLoader = classDynLoader;
	}
	
	public Class getClassCode()
	{
		return classCode;
	}

	void addInstance(Object obj)
	{
		if(arrInstances == null)
			arrInstances = new ArrayList<Object>();
		arrInstances.add(obj);
	}

	
//	Object makeNewInstance()
//	{
//		Object obj = null;
//		try
//		{
//			classDynLoader.inMakeNewInstance();
//			obj = classCode.newInstance();
//			if(obj != null)
//			{
//				if(arrInstances == null)
//					arrInstances = new ArrayList<Object>();
//				arrInstances.add(obj);
//			}
//			classDynLoader.outMakeNewInstance();
//			return obj;
//		}
//		catch (InstantiationException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IllegalAccessException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;		
//	}
	
	void removeAllInstances()
	{
		if(arrInstances != null)
		{
//			int nNbinstances = getNbInstances();
//			for(int nInstance=0; nInstance<nNbinstances; nInstance++)
//			{
//				Object obj = getInstance(nInstance);
//				// remove all copy parented by obj 
//			}
			arrInstances.clear();
			arrInstances = null;
		}
		classCode = null;
		classDynLoader = null;
	}
	
	int getNbInstances()
	{
		if(arrInstances != null)
		{
			return arrInstances.size();
		}
		return 0;
	}
	
	Object getInstance(int n)
	{
		if(arrInstances != null)
		{
			return arrInstances.get(n);
		}
		return null;
	}
	
	

	private Class classCode = null;
	private ClassDynLoader classDynLoader = null;	// Holds a ref; do not delete 
	private ArrayList<Object> arrInstances = null;	
}
