/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import javax.management.openmbean.OpenType;

public class CompositeTypeDescItem
{
	CompositeTypeDescItem(String csName, String csDescription, OpenType openType)
	{
		this.csName = csName;
		this.csDescription = csDescription;
		this.openType = openType;
	}
	
	String csName = null;
	String csDescription = null;
	OpenType openType = null;

}
