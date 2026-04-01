/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.expressions;

import generate.CBaseLanguageExporter;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaString extends CEntityString
{
	public CJavaString(CObjectCatalog cat, CBaseLanguageExporter out, char[] val)
	{
		super(cat, out, val);
	}
	public String ExportReference(int nLine)
	{
		String cs = "" ;
		boolean isspecialCharacters = false ;
		for (int i=0; i<carrValue.length;i++)
		{
			char b = carrValue[i] ;
			if (b=='"')
			{
				cs += "\\\"" ;
			}
			else if (b == '\\')
			{
				cs += "\\\\";
			}
			else if (b>255)
			{
				isspecialCharacters = true ;
				int n = (256 + b);
				String t = Integer.toHexString(n) ;
				cs += "\\u"+t ;
			}
			else if (b>127)
			{
				isspecialCharacters = true ;
				int n = b;
				String t = Integer.toHexString(n) ;
				cs += "\\u00"+t ;
			}
			else if (b>=32)
			{
				char c = b ;
				cs += c ;
			}
			else if (b==10)
			{
				cs += "\\n" ;
			}
			else if (b==13)
			{
				cs += "\\r" ;
			}
			else if (b==9)
			{
				cs += "\\t" ;
			}
			else if (b>=16)
			{
				isspecialCharacters = true ;
				int n = b;
				String t = Integer.toHexString(n) ;
				cs += "\\u00"+t ;
			}
			else if (b>=0)
			{
				isspecialCharacters = true ;
				int n = b;
				String t = Integer.toHexString(n) ;
				cs += "\\u000"+t ;
			}
		}
		if (isspecialCharacters)
		{
//			CGlobalEntityCounter.GetInstance().Count
		}
		return ("\"" + cs + "\"");
	}
	public boolean isValNeeded()
	{
		return false;
	}

}
