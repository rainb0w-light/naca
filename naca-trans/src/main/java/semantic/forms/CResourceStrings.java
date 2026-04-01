/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 11 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.ArrayList;


/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CResourceStrings
{
	public static String LANG_FRENCH = "FR" ;
	public static String LANG_GERMAN = "DE" ;
	public static String LANG_ITALIAN = "IT" ;
	public static String LANG_ENGLISH = "EN" ;
	public static String getOfficialLanguageCode(String lang)
	{
		lang = lang.trim() ;
		if (lang.endsWith("F"))
		{
			return LANG_FRENCH;
		}
		else if (lang.endsWith("D"))
		{
			return LANG_GERMAN;
		}
		else if (lang.endsWith("I"))
		{
			return LANG_ITALIAN;
		}
		else if (lang.endsWith("G"))
		{
			return LANG_ENGLISH;
		}
		else
		{
			return lang;
		}
	}
	
	protected ArrayList<String> langId = new ArrayList<String>() ;
	protected class CLocalizedText
	{
		public String csId = "" ;
		public HashMap<String, String> textTable = new HashMap<String, String>();
		public int length =0  ; 
	}
	public CResourceStrings(int nbLines, int nbCols)
	{
		nbCols = nbCols ;
		nbLines = nbLines ;
		lines = new CLocalizedText[nbLines+1][];
		tabTexts = new Hashtable<String, CLocalizedText>() ;
	}
	public void SetResourceText(int line, int col, String text, String langID, int length)
	{
		String csLang = getOfficialLanguageCode(langID) ; 
		if (!langId.contains(csLang))
		{
			langId.add(csLang) ;
		}
		CLocalizedText lText = GetResourceAt(line, col) ;
		lText.length = length ;
		lText.textTable.put(csLang, text) ;
	}
	public void SetResourceText(int line, int col, String text, String langID, String id, int length)
	{
		String csLang = getOfficialLanguageCode(langID) ; 
		if (!langId.contains(csLang))
		{
			langId.add(csLang) ;
		}
		CLocalizedText lText = GetResourceAt(line, col) ;
		lText.length = length ;
		lText.textTable.put(csLang, text) ;
		if (!id.equals(""))
		{
			lText.csId = id ;
			tabTexts.put(id, lText) ;
		}
	}
	protected CLocalizedText GetResourceAt(int line, int col)
	{
		CLocalizedText text = null ;
		if (lines[line] == null)
		{
			lines[line] = new CLocalizedText[nbCols+1] ;
		}
		text = lines[line][col] ;
		if (text == null)
		{
			text = new CLocalizedText() ;
			lines[line][col] = text;
		} 
		return text;
	}
	public abstract Element Export(Element parent, Document root);
	
	public String CreateName(String radical)
	{
		return radical + "_LABEL_" + lastIndex++;
	}
	protected int lastIndex = 0;
	protected int nbLines = 0 ;
	protected int nbCols = 0 ;
	protected CLocalizedText[][] lines = null ;
	protected Hashtable<String, CLocalizedText> tabTexts = null ;

	public Node ExportResource(String name, Document doc)
	{
		CLocalizedText res = tabTexts.get(name) ;
		if (res == null)
		{
			return null;
		}
		Element eText = doc.createElement("texts");
		int n = res.textTable.size() ;
		for (int i=0; i<n; i+=2)
		{
			String id = res.textTable.get(i) ;
			String text = res.textTable.get(i+1) ;
			Element e = doc.createElement("text");
			e.setAttribute("lang", id) ;
			eText.appendChild(e);
			e.appendChild(doc.createTextNode(text));
		}
		return eText ;
	}
	public abstract void FormatResource(String name) ;
	/**
	 * @param initialValue
	 * @return
	 */
	public abstract String ExportForField(String initialValue, String display) ; 
	
	public String ExportAllLangId()
	{
		String cs = "" ;
		for (int i = 0; i< langId.size(); i++)
		{
			if (i>0)
			{
				cs += ";" ;
			}
			cs += langId.get(i) ;
		}
		return cs ;
	}
	/**
	 * @param posLine
	 * @param posCol
	 * @return
	 */
	public boolean isExistingField(int line, int col, int length)
	{
		if (line > nbLines || col > nbCols)
		{
			return false ;
		}
		CLocalizedText text = null ;
		if (lines[line] == null)
		{
			return  false ;
		}
		text = lines[line][col] ;
		if (text == null)
		{
			return false ;
		} 
		if (text.length != length)
		{
			return false ;
		}
		return true ;
	}
}
