/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 7 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package utils;

import java.util.Vector;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityHierarchy
{

	public CEntityHierarchy()
	{
	}
	
//	public CEntityHierarchy(CEntityHierarchy hier)
//	{
//		if (hier != null)
//		{
//			Concat(hier) ;
//		}
//	}	
	protected Vector<String> ascendants = new Vector<String>() ;

	public void AddLevel(String level)
	{
		ascendants.addElement(level) ;
	}
	
	public boolean CheckAscendant(String name)
	{
		if (name.contains(";"))
		{
			String [] arr = name.split(";") ;
			return CheckAscendants(arr) ;
		}
		return ascendants.contains(name) && !ascendants.elementAt(ascendants.size()-1).equals(name);
	}	
	public boolean CheckAscendants(String[] arr)
	{
		boolean b = true ;
		for (String cs : arr)
		{
			b &= CheckAscendant(cs) ;
		}
		return b ;
	}	
	
	public String FindGoodName(CEntityHierarchy tab[], String curName, int index)
	{
		String radical = curName ;
		int n = curName.indexOf('$') ;
		if (n>0)
		{
			radical = curName.substring(0, n) ;
		}
		String goodName = "" ;
		for (int i = 0; i< ascendants.size()-1 ; i++) // hierarchy starts with object's name
		{
			boolean isfound = false ;
			String asc = ascendants.get(i);
			for (int j = 0; j<tab.length && !isfound; j++)
			{
				isfound |= tab[j].CheckAscendant(asc);
			}
			if (!isfound)
			{
				goodName = asc ;
				break ;
			}
		}
		if (goodName.equals(""))
		{
			return radical+"$"+index ;
		}
		return radical+"$"+goodName ;
	}
	
	public CEntityHierarchy Concat(CEntityHierarchy hier)
	{
		for (int i = 0; i<hier.ascendants.size(); i++)
		{
			ascendants.addElement(hier.ascendants.get(i));
		}		
		return this ;
	}
}
