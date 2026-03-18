/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.xml;

/*
 * Created on 26 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ValCursor
{
	public ValCursor()
	{
	}
	
	void setEnumVal(NamedNodeMap nodeMap)
	{
		nodeMap = nodeMap; 
	}
	
	public String getFirstVal()
	{
		nIndex = 0;
		return getNextVal();
	}
	
	public String getNextVal()
	{
		int nNbIndex = nodeMap.getLength();
		if(nIndex < nNbIndex)
		{
			Node node = nodeMap.item(nIndex);
			nIndex++;
			String cs = node.getNodeValue();
			return cs;
		}
		return null;
	}
	
	public Node getFirstParam()
	{
		nIndex = 0;
		return getNextParam();
	}
	
	public Node getNextParam()
	{
		int nNbIndex = nodeMap.getLength();
		if(nIndex < nNbIndex)
		{
			Node node = nodeMap.item(nIndex);
			nIndex++;
			return node;
		}
		return null;
	}
	
	
	
	NamedNodeMap nodeMap = null;
	int nIndex = 0;
}
