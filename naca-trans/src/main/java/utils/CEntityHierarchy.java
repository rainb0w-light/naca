/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils;

import java.util.Vector;

/**
 * @author sly
 *
 */
public class CEntityHierarchy
{

    /** Creates a new centity hierarchy instance. */
    public CEntityHierarchy()
    {
    }

//  public CEntityHierarchy(CEntityHierarchy hier)
//  {
//      if (hier != null)
//      {
//          Concat(hier) ;
//      }
//  }
    protected Vector<String> ascendants = new Vector<String>() ;

    /** Adds the level. */
    public void AddLevel(String level)
    {
        ascendants.addElement(level) ;
    }

    /** Executes the check ascendant operation. */
    public boolean CheckAscendant(String name)
    {
        if (name.contains(";"))
        {
            String [] arr = name.split(";") ;
            return CheckAscendants(arr) ;
        }
        return ascendants.contains(name) && !ascendants.elementAt(ascendants.size()-1).equals(name);
    }
    /** Executes the check ascendants operation. */
    public boolean CheckAscendants(String[] arr)
    {
        boolean b = true ;
        for (String cs : arr)
        {
            b &= CheckAscendant(cs) ;
        }
        return b ;
    }

    /** Finds the good name. */
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

    /** Executes the concat operation. */
    public CEntityHierarchy Concat(CEntityHierarchy hier)
    {
        for (int i = 0; i<hier.ascendants.size(); i++)
        {
            ascendants.addElement(hier.ascendants.get(i));
        }
        return this ;
    }
}
