/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

/** Provides composite data desc behavior. */
public class CompositeDataDesc
{
    /** Creates a new composite data desc instance. */
    public CompositeDataDesc(CompositeType compositeType)
    {
        this.compositeType = compositeType;
        map = new HashMap<String, Object>();
    }

    /** Sets the item value. */
    public void setItemValue(String csKey, Object oValue)
    {
        map.put(csKey, oValue);
    }

    /** Executes the generate composite data operation. */
    public CompositeData generateCompositeData()
    {
        try
        {
            CompositeDataSupport compositeData = new CompositeDataSupport(compositeType, map);
            return compositeData;
        }
        catch (OpenDataException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private CompositeType compositeType = null;
    Map<String, Object> map = null;
}
