/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lexer;

import java.util.Hashtable;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CConstantList
{
	protected CConstantList()
	{
	}
	
	public void Register(CReservedConstant cste)
	{
		tabConstants.put(cste.name, cste) ;
	}
	
	public CReservedConstant GetConstant(String name)
	{
		CReservedConstant kw = tabConstants.get(name);
		return kw ;
	}
	private Hashtable<String, CReservedConstant> tabConstants = new Hashtable<String, CReservedConstant>() ;
	
	//public static CReservedConstant  = new CReservedConstant(List, "") ; 
	//public static CReservedConstant  = new CReservedConstant(List, "") ; 
	//public static CReservedConstant  = new CReservedConstant(List, "") ; 
	//public static CReservedConstant  = new CReservedConstant(List, "") ; 
	//public static CReservedConstant  = new CReservedConstant(List, "") ; 
	//public static CReservedConstant  = new CReservedConstant(List, "") ; 
}
