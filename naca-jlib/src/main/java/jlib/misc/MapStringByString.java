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

import java.util.Hashtable;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: MapStringByString.java,v 1.1 2006/09/07 13:28:18 u930di Exp $
 */
public class MapStringByString
{
    private Hashtable<String, String> tab = null;

    /** Creates a new map string by string instance. */
    public MapStringByString()
    {
        tab = new Hashtable<String, String>();
    }

    /** Executes the get operation. */
    public String get(String csKey)
    {
        return tab.get(csKey);
    }

    /** Executes the put operation. */
    public void put(String csKey, String csValue)
    {
        tab.put(csKey, csValue);
    }
}
