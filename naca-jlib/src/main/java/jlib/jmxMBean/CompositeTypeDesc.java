/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.util.ArrayList;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public class CompositeTypeDesc
{
	public CompositeTypeDesc(String csName, String csDescription)
	{
		this.csName = csName;
		this.csDescription = csDescription;
	}
	
	public void addItem(String csName, String csDescription, OpenType openType)
	{
		CompositeTypeDescItem itemDesc = new CompositeTypeDescItem(csName, csDescription, openType);
		arrItemDesc.add(itemDesc);		
	}
	
	public CompositeType generateCompositeType()
	{
		try
		{
			int nNbItems = arrItemDesc.size();
			OpenType [] openTypes = new OpenType [nNbItems];
			String [] itemTypeNames = new String [nNbItems];
			String [] itemTypeDescriptions = new String [nNbItems];
			for(int n=0; n<nNbItems; n++)
			{
				CompositeTypeDescItem itemDesc = arrItemDesc.get(n);
				openTypes[n] = itemDesc.openType;
				itemTypeNames[n] = itemDesc.csName;
				itemTypeDescriptions[n] = itemDesc.csDescription;			
			}
			
			CompositeType compositeType = new CompositeType(
				csName,
			    csDescription,
			    itemTypeNames,
			    itemTypeDescriptions,
			    openTypes);
			return compositeType;
		}
		catch (OpenDataException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	private String csName = null;
	private String csDescription = null;
	private ArrayList<CompositeTypeDescItem> arrItemDesc = new ArrayList<CompositeTypeDescItem>(); 
}
