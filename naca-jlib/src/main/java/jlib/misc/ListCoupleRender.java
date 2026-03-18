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

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ListCoupleRender.java,v 1.5 2007/10/16 09:47:51 u930di Exp $
 */
public class ListCoupleRender
{	
	public static ListCoupleRender set()
	{
		ListCoupleRender l = new ListCoupleRender();
		return l;
	}
	
	public static ListCoupleRender set(String csTitle)
	{
		ListCoupleRender l = new ListCoupleRender(csTitle);
		return l;
	}
	
	private static String csValue = null;
	
	private ListCoupleRender()
	{
		csValue = "";
	}
	
	private ListCoupleRender(String csTitle)
	{
		csValue = csTitle + ": ";
	}
	
	public ListCoupleRender set(String csName, String csValue)
	{
		if(csValue != null)
			csValue += "; ";
		csValue += "(" + csName + ",'" + csValue +"')";
		return this;
	}
	
	public ListCoupleRender set(String csName, Integer nValue)
	{
		if(csValue != null)
			csValue += "; ";
		csValue += "(" + csName + ",'" + nValue +"')";
		return this;
	}
	
	public ListCoupleRender set(String csName, Short sValue)
	{
		if(csValue != null)
			csValue += "; ";
		csValue += "(" + csName + ",'" + sValue +"')";
		return this;
	}
	
	public ListCoupleRender set(String csName, Double dValue)
	{
		if(csValue != null)
			csValue += "; ";
		csValue += "(" + csName + ",'" + dValue +"')";
		return this;
	}
	
	public String toString()
	{
		if(csValue != null)
			return csValue;
		return "";
	}
}
