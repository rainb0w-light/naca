/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/**
 * 
 */
package utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: UnboundRefIdColl.java,v 1.1 2007/06/28 06:19:46 u930bm Exp $
 */
public class UnboundRefIdColl
{
	UnboundRefIdColl()
	{
	}
	
	UnboundRefId find(String csName)
	{
		return hash.get(csName);
	}
	
	void add(int nLine, String csName, String csFile)
	{
		UnboundRefId ref = new UnboundRefId(nLine, csFile); 
		hash.put(csName, ref);
	}
	
	Enumeration<String> getKeys()
	{
		return hash.keys();
	}
	
	UnboundRefId getVal(String csName)
	{
		return hash.get(csName);
	}
	
	Hashtable<String, UnboundRefId> hash = new Hashtable<String, UnboundRefId>(); 
}
