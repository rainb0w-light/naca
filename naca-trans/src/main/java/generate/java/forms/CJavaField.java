/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
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
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaField extends CEntityResourceField
{

	/**
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CJavaField(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp)
	{
		super(l, name, cat, lexp);
	}
	public String ExportReference(int nLine)
	{
		String cs = "" ;
		if (of != null)
		{
			cs += of.ExportReference(getLine());
			cs += ".";
		}
		cs += FormatIdentifier(GetName()) ;
		return cs ;
	}
	protected void DoExport()
	{
		String fieldname = FormatIdentifier(GetName()) ;
//		String length = eField.getAttribute("Length");
//		String col = eField.getAttribute("Col");
//		String line = eField.getAttribute("Line");
		if (!fieldname.equals(""))
		{
			String displayName = FormatIdentifier(csDisplayName) ;
			if (displayName.equals(""))
			{
				displayName = fieldname ;
			} 
			String cs = "Edit " + fieldname + " = declare.edit(\""+displayName+"\", "+nLength+")" ;
//			if (m_occurs > 0)
//			{
//				cs += ".occurs("+ m_occurs +")" ;
//			}
			if (!csInitialValue.equals(""))
			{
				String display = FormatIdentifier(csInitialValue) ;
				String res = resourceStrings.ExportForField(csInitialValue, display) ;
				WriteLine(res) ;
				cs += ".initialValue("+display+")" ;
			}
//			if (!csColor.equals(""))
//			{
//				cs += ".color(MapFieldAttrColor."+ csColor+")" ;
//			}
//			if (!csHighLight.equals(""))
//			{
//				cs += ".highLighting(MapFieldAttrHighlighting."+ csHighLight+")" ;
//			}
//			if (!csBrightness.equals(""))
//			{
//				cs += ".intensity(MapFieldAttrIntensity."+ csBrightness+")" ;
//			}
//			if (!csProtection.equals(""))
//			{
//				cs += ".protection(MapFieldAttrProtection."+ csProtection+")" ;
//			}
			if (!csFillValue.equals(""))
			{
				cs += ".justifyFill(MapFieldAttrFill."+ csFillValue+")" ;
			}
			if (bRightJustified)
			{
				cs += ".justifyRight()" ;
			}
//			if (bCursor)
//			{
//				cs += ".setCursor(true)" ;
//			}
//			if (bModified)
//			{
//				cs += ".setModified()" ;
//			}
			if (!csDevelopableFlagMark.equals(""))
			{
				cs += ".setDevelopableMark(\"" + csDevelopableFlagMark + "\")" ;
			}
			if (!csFormat.equals(""))
			{
				cs += ".format(\"" + csFormat + "\")" ;
			}
			cs += ".edit() ;" ;
			WriteLine(cs);
		}
		if (lstChildren.size()> 0)
		{
			StartOutputBloc() ;
			ExportChildren();
			EndOutputBloc() ;
		}
	}

	public Element DoXMLExport(Document doc, CResourceStrings res)
	{
		Element ef;
		if (mode == FieldMode.TITLE)
		{
			ef = doc.createElement("title") ;
		}
		else if (mode == FieldMode.SWITCH)
		{
			ef = doc.createElement("switch") ;
			ef.setAttribute("linkedvalue", FormatIdentifier(GetDisplayName())) ;
			ef.setAttribute("name", FormatIdentifier(GetDisplayName())) ;
			ef.setAttribute("length", String.valueOf(nLength)) ;
			for  (int i=0; i<arrSwitchCaseElement.size(); i++)
			{
				CSwitchCaseElement el = arrSwitchCaseElement.get(i) ;
				Element eCase ;
				if (el.val != null)
				{
					eCase = doc.createElement("case") ;
					ef.appendChild(eCase) ;
					eCase.setAttribute("value", el.val) ;
				}
				else if (el.protection != null)
				{
					eCase = doc.createElement("case") ;
					ef.appendChild(eCase) ;
					eCase.setAttribute("protection", el.protection) ;
				}
				else
				{
					eCase = doc.createElement("default") ;
					ef.appendChild(eCase) ;
				}
				Element etag = (Element)doc.importNode(el.tag, true) ;
				eCase.appendChild(etag) ;
			}
			return ef ;
		}
		else
		{
			ef = doc.createElement("edit") ;
		}
		
		// PJD Other params
		//ef.setAttribute("sourceline", "" + getLine()) ;
		//ef.setAttribute("original_name", "" + GetName()) ;
		
		//ef.setAttribute("", "" + getLine()) ;
		
		if (!csInitialValue.equals(""))
		{
//			res.GetResource;
			//ef.setAttribute("InitialValue", initialValue) ;
			ef.appendChild(res.ExportResource(csInitialValue, doc)) ;
		}

		
		if (mode == FieldMode.CHECKBOX)
		{
			ef.setAttribute("type", "checkbox") ;
			ef.setAttribute("valueOn", csCheckBoxValueOn);
			ef.setAttribute("valueOff", csCheckBoxValueOff);
		}
		else if (mode == FieldMode.ACTIVE_CHOICE)
		{
			ef.setAttribute("type", "activeChoice") ;
			ef.setAttribute("activeChoiceValue", csActiveChoiceValue);
			ef.setAttribute("activeChoiceTarget", csActiveChoiceTarget);
			ef.setAttribute("activeChoiceSubmit", bActiveChoiceSubmit?"true":"false");
		}
		else if (mode == FieldMode.LINKED_ACTIVE_CHOICE)
		{
			ef.setAttribute("type", "linkedActiveChoice") ;
			ef.setAttribute("activeChoiceLink", FormatIdentifier(csActiveChoiceValue));
			ef.setAttribute("activeChoiceTarget", csActiveChoiceTarget);
			ef.setAttribute("activeChoiceSubmit", bActiveChoiceSubmit?"true":"false");
		}
		if (mode == FieldMode.HIDDEN)
		{
			ef.setAttribute("type", "hidden") ;
			ef.setAttribute("length", "0") ;
			ef.setAttribute("line", "0") ;
			ef.setAttribute("col", "0") ;
		}
		else
		{
			ef.setAttribute("length", String.valueOf(nLength)) ;
			ef.setAttribute("line", String.valueOf(nPosLine)) ;
			ef.setAttribute("col", String.valueOf(nPosCol)) ;
		}
		ef.setAttribute("linkedvalue", FormatIdentifier(GetDisplayName())) ;
		if (!GetName().equals(""))
		{
			String csName = FormatIdentifier(GetDisplayName());
			ef.setAttribute("name", csName) ;
			String csNameCopy = FormatIdentifier(GetName());
			if (!csNameCopy.equals(csName))
				ef.setAttribute("namecopy", FormatIdentifier(GetName())) ;
		}
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
			ef.setAttribute("intensity", csBrightness.toLowerCase());
		}
		if (!csProtection.equals(""))
		{
			ef.setAttribute("protection", csProtection.toLowerCase());
		}
		if (bCursor)
		{
			ef.setAttribute("cursor", "true") ;
		}
		if (bModified)
		{
			ef.setAttribute("modified", "true") ;
		}
		if (bReplayMutable)
		{
			ef.setAttribute("replayMutable", "true") ;
		}
		if (bRightJustified)
			ef.setAttribute("justify", "right") ;
		else
			ef.setAttribute("justify", "left") ;
		ef.setAttribute("fill", csFillValue.toLowerCase()) ;
		return ef ;
	}
	public boolean IsNeedDeclarationInClass()
	{
		return false ;
	}
	public String ExportWriteAccessorTo(String value)
	{
		// unsued		
		return "" ;
	}
	public boolean isValNeeded()
	{
		return true;
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public boolean IsEntryField()
	{
		return true;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseExternalEntity#GetTypeDecl()
	 */
	public String GetTypeDecl()
	{
		return "" ; // unused
	}

}
