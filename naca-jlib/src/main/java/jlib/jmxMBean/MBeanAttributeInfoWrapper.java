/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.lang.reflect.Method;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;

/** Provides mbean attribute info wrapper behavior. */
public class MBeanAttributeInfoWrapper
{
    /** Creates a new mbean attribute info wrapper instance. */
    public MBeanAttributeInfoWrapper(String csName, String csDescription, Method getter, Method setter)
    {
        try
        {
            attribute = new MBeanAttributeInfo(csName, csDescription, getter, setter);
        }
        catch (IntrospectionException e)
        {
            e.printStackTrace();
        }
        this.getter = getter;
        this.setter = setter;
    }

    MBeanAttributeInfo getAttribute()
    {
        return attribute;
    }

    Method getMethodGetter()
    {
        return getter;
    }

    Method getMethodSetter()
    {
        return setter;
    }

    private Method getter = null;
    private Method setter = null;
    private MBeanAttributeInfo attribute = null;
}
