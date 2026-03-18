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

import java.util.Hashtable;
import java.util.Vector;

import jlib.xml.Tag;

public class CTransApplicationGroup
{
	public CTransApplicationGroup(BaseEngine engine)
	{
		engine = engine;
	}
	
	public enum EProgramType
	{
		TYPE_ONLINE,
		TYPE_BATCH,
		TYPE_CALLED,
		TYPE_INCLUDED,
		TYPE_MAP
	};
	
	public String csName ;
	public Hashtable<String, Tag> tabApplication = new Hashtable<String, Tag>() ;
	public Vector<String> arrApplications = new Vector<String>() ;
	public EProgramType eType ;
	public String csInputPath = "" ;
	public String csOutputPath = "" ;
	public String csInterPath = "" ;
	
	private BaseEngine engine = null ;
	
	public BaseEngine getEngine()
	{
		return engine ;
	}
}