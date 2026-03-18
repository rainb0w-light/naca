/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.lang.reflect.Method;

import javax.management.MBeanOperationInfo;

public class MBeanOperationInfoWrapper
{
	public MBeanOperationInfoWrapper(String csDescription, Method method)
	{
		operation = new MBeanOperationInfo(csDescription, method);
		this.method = method;
	}
	
	MBeanOperationInfo getOperation()
	{
		return operation;
	}
	
	Method getMethod()
	{
		return method;
	}
	
	private Method method = null;
	MBeanOperationInfo operation = null;

}
