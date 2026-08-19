/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package jlib.misc;

import java.util.ArrayList;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: StringArray.java,v 1.1 2006/09/07 13:28:18 u930di Exp $
 */
public class StringArray
{
    private ArrayList<String> arr = null;

    /** Creates a new string array instance. */
    public StringArray()
    {
        arr = new ArrayList<String>();
    }

    /** Executes the add operation. */
    public void add(String csElem)
    {
        arr.add(csElem);
    }

    /** Executes the size operation. */
    public int size()
    {
        return arr.size();
    }

    /** Executes the get operation. */
    public String get(int n)
    {
        return arr.get(n);
    }
}
