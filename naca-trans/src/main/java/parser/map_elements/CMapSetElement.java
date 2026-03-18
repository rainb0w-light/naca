/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 30 juil. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.map_elements;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import jlib.xml.Tag;
import jlib.xml.TagCursor;

import lexer.*;
import lexer.BMS.CBMSConstantList;
import lexer.BMS.CBMSKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import parser.BMS.CBMSElement;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseResourceEntity;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import semantic.forms.CResourceStrings;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMapSetElement extends CBMSElement
{

	/**
	 * @param line
	 */
	public CMapSetElement(String name, int line)
	{
		super(name, line);
	}

	public CBaseResourceEntity DoSemanticAnalysis(CDataEntity parent, CBaseEntityFactory factory)
	{
		CEntityResourceFormContainer eFC = factory.NewEntityFormContainer(getLine(), getName(), false) ;
		ListIterator i = children.listIterator() ;
		try
		{
			CBMSElement le = (CBMSElement)i.next() ;
			while (le != null)
			{
				if (le.GetType()==EBMSElementType.MAP)
				{
					if (resStrings != null)
					{
						le.SetResourceStrings(resStrings) ;
					}
					CEntityResourceForm form = (CEntityResourceForm)le.DoSemanticAnalysis(eFC, factory) ;
					if (resStrings == null)
					{
						resStrings = le.GetResourceStrings(); 
					}
					if (form != null)
					{
						form.of = eFC ;
						eFC.AddForm(form) ;
						form.setResourceName(getName()) ;
						if (form.GetName().endsWith("F"))
						{
//							arrAccessors.add(le.GetName()) ;
							form.SetReferences(arrAccessors) ;
						}
					}
					else if (form == null)
					{
						arrAccessors.add(le.getName());
					}
				}
				le = (CBMSElement)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
		eFC.resStrings = resStrings ;
		return eFC ;
	}
	protected ArrayList<String> arrAccessors = new ArrayList<String>() ;
	
	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element DoExportCustom(Document root)
	{
		Element eMS = root.createElement("MapSet") ;
		eMS.setAttribute("Mode", mode) ;
		eMS.setAttribute("Language", language) ;
		return eMS ;
	}

	/* (non-Javadoc)
	 * @see parser.CBMSElement#InterpretKeyword(lexer.CReservedKeyword, lexer.CTokenList)
	 */
	protected boolean InterpretKeyword(CReservedKeyword kw, CTokenList lstTokens)
	{
		if (kw == CBMSKeywordList.TYPE )
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() != CBMSConstantList.MAP)
			{
				Transcoder.logError(getLine(), "Expecting MAP") ;
				return false ;
			}
			GetNext() ;
		}
		else if (kw == CBMSKeywordList.MODE)
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.INOUT)
			{
				mode = tok.GetValue() ;
			}
			else
			{
				Transcoder.logError(getLine(), "Expecting INOUT") ;
				return false ; 
			}
			GetNext() ;
		}
		else if (kw == CBMSKeywordList.LANG)
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.COBOL)
			{
				language = tok.GetValue() ;
			}
			else
			{
				Transcoder.logError(getLine(), "Expecting COBOL") ;
				return false ; 
			}
			GetNext() ;
		}
//		else if (kw == CBMSKeywordList.)
//		{
//			CBaseToken tok = GetCurrentToken() ;
//			if (tok.GetConstant() == CBMSConstantList.)
//			{
//				m_ = tok.GetValue() ;
//			}
//			else
//			{
//				return false ; 
//			}
//			GetNext() ;
//		}
//		else if (kw == CBMSKeywordList.)
//		{
//			CBaseToken tok = GetCurrentToken() ;
//			if (tok.GetConstant() == CBMSConstantList.)
//			{
//				m_ = tok.GetValue() ;
//			}
//			else
//			{
//				return false ; 
//			}
//			GetNext() ;
//		}
//		else if (kw == CBMSKeywordList.)
//		{
//			CBaseToken tok = GetCurrentToken() ;
//			if (tok.GetConstant() == CBMSConstantList.)
//			{
//				m_ = tok.GetValue() ;
//			}
//			else
//			{
//				return false ; 
//			}
//			GetNext() ;
//		}
//		else if (kw == CBMSKeywordList.)
//		{
//			CBaseToken tok = GetCurrentToken() ;
//			if (tok.GetConstant() == CBMSConstantList.)
//			{
//				m_ = tok.GetValue() ;
//			}
//			else
//			{
//				return false ; 
//			}
//			GetNext() ;
//		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting keyword : "+kw.name) ;
			return false ;
		}
		return true ;
	}
	
	String mode = "" ;
	String language = "" ;
	/* (non-Javadoc)
	 * @see parser.CBMSElement#GetType()
	 */
	public EBMSElementType GetType()
	{
		return EBMSElementType.MAPSET ;
	}

	protected CResourceStrings resStrings = null ;
	public CResourceStrings GetResourceStrings()
	{
		return resStrings ;
	}
	public void SetResourceStrings(CResourceStrings res)
	{
		resStrings = res ;
	}
	
	public CBMSElement loadTagParameters(Tag tagCurrent)
	{
		language = tagCurrent.getVal("Language");
		int nLine = tagCurrent.getValAsInt("Line");
		setLine(nLine);
		mode = tagCurrent.getVal("Mode");
		setName(tagCurrent.getVal("Name"));
		
		return loadInternalTags(tagCurrent);
	}
		
	public CBMSElement parseXMLResource(Tag tag)
	{
		String csName = tag.getName();
		CBMSElement elem = null;
		if(csName.equalsIgnoreCase("Map"))
		{
			elem = new CMapElement("", 0);
			elem.loadTagParameters(tag);
		}
		return elem;
	}	
	
	
	private CBMSElement loadInternalTags(Tag tagCurrent)
	{
		TagCursor curChild = new TagCursor();
		Tag tagChild = tagCurrent.getFirstChild(curChild);
		while(tagChild != null)
		{
			CBMSElement elem = parseXMLResource(tagChild);
			if(elem != null)
				AddElement(elem);
			tagChild = tagCurrent.getNextChild(curChild);
		}
		return this;
	}
	
	
	public CBMSElement loadFromRES(String csName)
	{
		language = "COBOL";
		mode = "INOUT";
		setName(csName);
		setLine(1);
		return this;			
	}
}
