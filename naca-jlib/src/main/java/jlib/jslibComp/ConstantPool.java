/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.jslibComp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ConstantPool.java,v 1.1 2008/04/01 07:10:30 u930di Exp $
 */
public class ConstantPool
{
	private ArrayList<ConstantItem> arr = null;
	
	public void add(String csId, String csValue)
	{
		if(arr == null)
			arr = new ArrayList<ConstantItem>();
		
		ConstantItem item = new ConstantItem(csId, csValue);
		arr.add(item);
	}
	
	public Collection<ConstantItem> getCollection()
	{
		return arr;
	}
	
}
