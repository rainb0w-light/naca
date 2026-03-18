/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.lang.reflect.Method;

import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;

public class OpenMBeanAttributeInfoWrapper
{
	public OpenMBeanAttributeInfoWrapper(String csName, String csDescription, OpenMBeanAttributeInfoSupport openType, Method getter, Method setter)
	{
		this.openType = openType;
		this.getter = getter;
		this.setter = setter;
	}
	
	OpenMBeanAttributeInfo getAttribute()
	{
		return openType;
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
	private OpenMBeanAttributeInfoSupport openType = null;
}
