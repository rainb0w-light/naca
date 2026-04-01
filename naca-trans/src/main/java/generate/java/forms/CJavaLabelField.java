/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.forms;

import generate.CBaseLanguageExporter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.forms.CEntityResourceField;
import semantic.forms.CResourceStrings;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaLabelField extends CEntityResourceField
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param lexp
	 */
	public CJavaLabelField(int l, CObjectCatalog cat, CBaseLanguageExporter lexp)
	{
		super(l, "", cat, lexp);
	}
	public boolean IsEntryField()
	{
		return false;
	}
	public Element DoXMLExport(Document doc, CResourceStrings res)
	{
		Element ef ;
		if (mode == FieldMode.TITLE)
		{
			ef = doc.createElement("title") ;
		}
		else if (mode == FieldMode.HIDDEN)
		{
			return null ;
		}
		else if (mode == FieldMode.ACTIVE_CHOICE)
		{
			ef = doc.createElement("label") ;
			ef.setAttribute("type", "activeChoice") ;
			ef.setAttribute("activeChoiceValue", csActiveChoiceValue);
			ef.setAttribute("activeChoiceTarget", csActiveChoiceTarget);
			ef.setAttribute("activeChoiceSubmit", isactiveChoiceSubmit ?"true":"false");
		}
		else if (mode == FieldMode.LINKED_ACTIVE_CHOICE)
		{
			ef = doc.createElement("label") ;
			ef.setAttribute("type", "linkedActiveChoice") ;
			ef.setAttribute("activeChoiceLink", FormatIdentifier(csActiveChoiceValue));
			ef.setAttribute("activeChoiceTarget", csActiveChoiceTarget);
			ef.setAttribute("activeChoiceSubmit", isactiveChoiceSubmit ?"true":"false");
		}
		else
		{
			ef = doc.createElement("label") ;
		}
		ef.setAttribute("length", String.valueOf(nLength)) ;
		ef.setAttribute("line", String.valueOf(nPosLine)) ;
		ef.setAttribute("col", String.valueOf(nPosCol)) ;
		if (!csInitialValue.equals(""))
		{
//			res.GetResource;
			//ef.setAttribute("InitialValue", initialValue) ;
			ef.appendChild(res.ExportResource(csInitialValue, doc)) ;
		}
		if (!csDisplayName.equals(""))
		{
			ef.setAttribute("name", csDisplayName);
		}
//		if (!GetName().equals(""))
//		{
//			ef.setAttribute("Name", GetName()) ;
//		}
		if (!csColor.equals(""))
		{
			ef.setAttribute("color", csColor.toLowerCase());
		}
		if (!csHighLight.equals(""))
		{
			ef.setAttribute("highlighting", csHighLight.toLowerCase());
		}
		if (!csBrightness.equals(""))
		{
			ef.setAttribute("brightness", csBrightness.toLowerCase());
		}
//		if (!protection.equals(""))
//		{
//			ef.setAttribute("Protection", protection);
//		}
//		if (!fillValue.equals(""))
//		{
//			ef.setAttribute("FillValue", fillValue);
//		}
//		if (!justify.equals(""))
//		{
//			ef.setAttribute("Justify", justify);
//		}
//		for (int i=0; i<arrAttrib.size(); i++)
//		{
//			String cs = arrAttrib.get(i);
//			Element e = doc.createElement("Attribute");
//			ef.appendChild(e);
//			e.setAttribute("Value", cs);			
//		}
//		for (int i=0; i<arrJustify.size(); i++)
//		{
//			String cs = arrJustify.get(i);
//			Element e = doc.createElement("Justify");
//			ef.appendChild(e) ;
//			e.setAttribute("Value", cs);			
//		}
		return ef ;
	}
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public String ExportReference(int nLine)
	{
		// unsued
		return "" ;
	}
	public String ExportWriteAccessorTo(String value)
	{
		// unused
		return "" ;
	}
	public boolean isValNeeded()
	{
		return false;
	}

	protected void DoExport()
	{
		// unused
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseExternalEntity#GetTypeDecl()
	 */
	public String GetTypeDecl()
	{
		return "" ; // unsued
	}
}
