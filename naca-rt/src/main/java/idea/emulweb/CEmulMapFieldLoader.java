/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.emulweb;


import idea.manager.CMapFieldLoader;

import java.util.Hashtable;

import jlib.misc.MapStringByString;

/*
 * Created on 20 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEmulMapFieldLoader extends CMapFieldLoader
{
	MapStringByString tabValues = new MapStringByString() ;
	Hashtable<String, Boolean> tabModified = new Hashtable<String, Boolean>() ;
	String csIdPage = null;
	
	public String getFieldValue(String fieldName)
	{
		String cs = tabValues.get(fieldName) ;
		if (cs == null)
		{
			cs = "" ;
		}
		return cs ;
	}

	public void setFieldValue(String fieldName, String value, boolean modified)
	{
		tabValues.put(fieldName, value) ;
		Boolean upd = new Boolean(modified) ;
		tabModified.put(fieldName, upd) ;
	}

	public void reset()
	{
		tabValues = new MapStringByString() ;
		tabModified = new Hashtable<String, Boolean>() ;
		csIdPage = null;
	}

	/* (non-Javadoc)
	 * @see CBaseMapFieldLoader#getIDPage()
	 */
	public String getIDPage()
	{
		return csIdPage ;
	}
	
	public void setIDPage(String csIdPage)
	{
		csIdPage = csIdPage;
	}

	/* (non-Javadoc)
	 * @see CBaseMapFieldLoader#isFieldModified(java.lang.String)
	 */
	public boolean isFieldModified(String fieldName)
	{
		Boolean b = tabModified.get(fieldName) ;
		if (b != null)
		{
			return b.booleanValue() ;
		}
		else
		{
			return false;
		}
	}

}
