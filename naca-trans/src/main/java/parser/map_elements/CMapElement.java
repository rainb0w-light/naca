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


import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import jlib.misc.NumberParser;
import jlib.xml.Tag;
import jlib.xml.TagCursor;

import lexer.*;
import lexer.BMS.CBMSConstantList;
import lexer.BMS.CBMSKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import parser.CBaseElement;
import parser.BMS.CBMSElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import semantic.forms.CResourceStrings;
import utils.NacaTransAssertException;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMapElement extends CBMSElement
{

	/**
	 * @param name
	 * @param line
	 */
	public CMapElement(String name, int line)
	{
		super(name, line);
	}

	/* (non-Javadoc)
	 * @see parser.CBMSElement#DoExportCustom(org.w3c.dom.Document)
	 */
	protected Element DoExportCustom(Document root)
	{
		Element eMS = root.createElement("Map") ;
		Element eAttr = root.createElement("Attributes") ;
		eMS.appendChild(eAttr) ;
		eAttr.setAttribute("SizeCol", String.valueOf(size_Col)) ;
		eAttr.setAttribute("SizeLine", String.valueOf(size_Line)) ;
		eAttr.setAttribute("Line", line.name) ;
		eAttr.setAttribute("Column", column.name) ;
		eAttr.setAttribute("Data", data.name) ;
		eAttr.setAttribute("TIOAPFX", tIOAPFX.name) ;
		eAttr.setAttribute("OBFMT", oBFMT.name) ;
		for (int i=0; i<arrCTRL.size(); i++)
		{
			String val = arrCTRL.get(i) ;
			Element e = root.createElement("CTRL") ;
			e.setAttribute("Value", val);
			eAttr.appendChild(e) ; 
		}
		for (int i=0; i<arrMAPATTS.size(); i++)
		{
			String val = arrMAPATTS.get(i) ;
			Element e = root.createElement("MAPATTS") ;
			e.setAttribute("Value", val);
			eAttr.appendChild(e) ; 
		}
		for (int i=0; i<arrJUSTIFY.size(); i++)
		{
			String val = arrJUSTIFY.get(i) ;
			Element e = root.createElement("JUSTIFY") ;
			e.setAttribute("Value", val);
			eAttr.appendChild(e) ; 
		}
		for (int i=0; i<arrDSATTS.size(); i++)
		{
			String val = arrDSATTS.get(i) ;
			Element e = root.createElement("DSATTS") ;
			e.setAttribute("Value", val);
			eAttr.appendChild(e) ; 
		}
		return eMS ;
	}


	/* (non-Javadoc)
	 * @see parser.CBMSElement#InterpretKeyword(lexer.CReservedKeyword, lexer.CTokenList)
	 */
	protected boolean InterpretKeyword(CReservedKeyword kw, CTokenList lstTokens)
	{
		if (kw == CBMSKeywordList.SIZE)
		{ //SIZE=(024,080)
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType() != CTokenType.LEFT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting LEFT_BRACKET") ;
				return false ;
			}
			tok = GetNext() ;
			if (tok.GetType() != CTokenType.NUMBER)
			{
				Transcoder.logError(getLine(), "Expecting NUMBER") ;
				return false ;
			}
			size_Line = tok.GetIntValue() ;
			tok = GetNext() ;
			if (tok.GetType() != CTokenType.COMMA)
			{
				Transcoder.logError(getLine(), "Expecting COMMA") ;
				return false ;
			}
			tok = GetNext();
			if (tok.GetType() != CTokenType.NUMBER)
			{
				Transcoder.logError(getLine(), "Expecting NUMBER") ;
				return false ;
			}
			size_Col = tok.GetIntValue() ;
			tok = GetNext() ;
			if (tok.GetType() != CTokenType.RIGHT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting RIGHT_BRACKET") ;
				return false ;
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.LINE)
		{ //LINE=NEXT
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.NEXT)
			{
				line = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting for LINE : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.COLUMN)
		{ // COLUMN=SAME
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.SAME)
			{
				column = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting for COLUMN : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.DATA)
		{ // DATA=FIELD
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.FIELD)
			{
				data = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting for DATA : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.OBFMT)
		{ //OBFMT=NO
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.NO)
			{
				oBFMT = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting for OBFMT : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.TIOAPFX)
		{ // TIOAPFX=YES
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.YES)
			{
				tIOAPFX = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting for TIOAPFX : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.CTRL)
		{ // CTRL=(HONEOM,FREEKB,ALARM,FRSET)
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType()!= CTokenType.LEFT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting LEFT_BRACKET") ;
				return false ;
			}
			tok = GetNext();
			boolean bDone = false ;
			while (!bDone)
			{
				tok = GetCurrentToken();
				if (tok.GetConstant() == CBMSConstantList.HONEOM ||
					tok.GetConstant() == CBMSConstantList.FREEKB || 
					tok.GetConstant() == CBMSConstantList.ALARM ||
					tok.GetConstant() == CBMSConstantList.FRSET || 
					tok.GetConstant() == CBMSConstantList.L80)
				{
					arrCTRL.add(tok.GetValue()) ;
				}
				else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					bDone = true ;
				}
				else if (tok.GetType() == CTokenType.COMMA)
				{
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting for CTRL : " + tok.GetValue()) ;
					return false ; 
				}
				StepNext() ;
			}
		}
		else if (kw == CBMSKeywordList.MAPATTS)
		{ // MAPATTS=(COLOR,PS,HILIGHT,VALIDN)
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType()!= CTokenType.LEFT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting LEFT_BRACKET") ;
				return false ;
			}
			tok = GetNext();
			boolean bDone = false ;
			while (!bDone)
			{
				tok = GetCurrentToken();
				if (tok.GetKeyword() == CBMSKeywordList.COLOR ||
					tok.GetConstant() == CBMSConstantList.PS || 
					tok.GetKeyword() == CBMSKeywordList.HILIGHT ||
					tok.GetConstant() == CBMSConstantList.VALIDN)
				{
					arrMAPATTS.add(tok.GetValue()) ;
				}
				else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					bDone = true ;
				}
				else if (tok.GetType() == CTokenType.COMMA)
				{
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting for MAPATTS : " + tok.GetValue()) ;
					return false ; 
				}
				StepNext() ;
			}
		}
		else if (kw == CBMSKeywordList.DSATTS)
		{ // DSATTS=(COLOR,PS,HILIGHT,VALIDN)
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType()!= CTokenType.LEFT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting LEFT_BRACKET") ;
				return false ;
			}
			tok = GetNext();
			boolean bDone = false ;
			while (!bDone)
			{
				tok = GetCurrentToken();
				if (tok.GetKeyword() == CBMSKeywordList.COLOR ||
					tok.GetConstant() == CBMSConstantList.PS || 
					tok.GetKeyword() == CBMSKeywordList.HILIGHT ||
					tok.GetConstant() == CBMSConstantList.VALIDN)
				{
					arrDSATTS.add(tok.GetValue()) ;
				}
				else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					bDone = true ;
				}
				else if (tok.GetType() == CTokenType.COMMA)
				{
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting for DSATTS : " + tok.GetValue()) ;
					return false ; 
				}
				StepNext() ;
			}
		}
		else if (kw == CBMSKeywordList.JUSTIFY)
		{ // JUSTIFY=(LEFT)
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType()!= CTokenType.LEFT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting LEFT_BRACKET") ;
				return false ;
			}
			tok = GetNext();
			boolean bDone = false ;
			while (!bDone)
			{
				tok = GetCurrentToken();
				if (tok.GetConstant() == CBMSConstantList.LEFT ||
					tok.GetConstant() == CBMSConstantList.FIRST || 
//					tok.GetConstant() == CBMSConstantList. ||
					tok.GetConstant() == CBMSConstantList.BLANK)
				{
					arrJUSTIFY.add(tok.GetValue()) ;
				}
				else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					bDone = true ;
				}
				else if (tok.GetType() == CTokenType.COMMA)
				{
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting for JUSTIFY : " + tok.GetValue()) ;
					return false ; 
				}
				StepNext() ;
			}
		}
//		else if (kw == CBMSKeywordList.)
//		{ // 
//			CBaseToken tok = GetCurrentToken() ;
//			if (tok.GetType()!= CTokenType.LEFT_BRACKET)
//			{
//				return false ;
//			}
//			tok = GetNext();
//			boolean bDone = false ;
//			while (!bDone)
//			{
//				tok = GetCurrentToken();
//				if (tok.GetConstant() == CBMSConstantList. ||
//					tok.GetConstant() == CBMSConstantList. || 
//					tok.GetConstant() == CBMSConstantList. ||
//					tok.GetConstant() == CBMSConstantList.)
//				{
//					arr.add(tok.add(tok.GetValue()) ;
//				}
//				else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
//				{
//					bDone = true ;
//				}
//				else if (tok.GetType() == CTokenType.COMMA)
//				{
//				}
//				else
//				{
//					return false ; 
//				}
//				GetNext() ;
//			}
//		}
//		else if (kw == CBMSKeywordList.)
//		{ // 
//			CBaseToken tok = GetCurrentToken() ;
//			if (tok.GetConstant() == CBMSConstantList.)
//			{
//				m_ = tok.GetConstant();
//			}
//			else
//			{
//				return false ; 
//			}
//			GetNext() ;
//		}
		else if (kw == CBMSKeywordList.TRAILER)
		{ // 
			CBaseToken tok = GetCurrentToken() ;
			trailer = tok.GetValue();
			GetNext() ;
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting keyword : " + kw.name) ;
			return false ;
		}
		return true ;
	}

	public CBaseLanguageEntity DoSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityResourceFormContainer container = (CEntityResourceFormContainer)parent ;
		String csLang = "";
		csLang += getName().charAt(6) ;
		boolean bStore = false ;
		if (csLang.equals("F"))
		{
			bStore = true ;
		}
		else if (!csLang.equals("D") && !csLang.equals("I") && !csLang.equals("G") && !csLang.equals("N"))
		{
			//Transcoder.warn("WARNING : unexpected lang ID : " + csLang) ;
			bStore = true ;
		}
		else
		{
			bStore = false ;
		}
		
		boolean bFirstForm = false ;
		if (resStrings == null)
		{
			resStrings = factory.NewResourceString(size_Line, size_Col);
			bFirstForm = true ;
		}
		CEntityResourceForm ef= null ; 
		if (bStore)
		{
//			if (csLang.equals("F"))
//			{
//				ef = factory.NewEntityForm(getLine(), container.GetName(), false) ;
//			}
//			else
//			{
				ef = factory.NewEntityForm(getLine(), getName(), false) ;
//			}
		 	factory.programCatalog.RegisterMap(ef) ;
			ef.SetSize(size_Col, size_Line) ;
			ef.of = container ;
		}
		
		if (bFindArrays)
		{
			ManageArray() ;
		}
		
		ListIterator i = children.listIterator() ;
		try
		{
			Object o = i.next() ;
			CBMSElement le = (CBMSElement)o ;
			while (le != null)
			{
				if (le.GetType()==EBMSElementType.FIELD)
				{
					le.SetResourceStrings(resStrings) ;
					CEntityResourceField field = (CEntityResourceField)le.DoSemanticAnalysis(ef, factory) ;
					if (field.nLength>0)
					{
						field.of = container ;
						if (field.nPosCol + field.nLength > size_Col+1)
						{
							Transcoder.logWarn(0, "Form : "+name+" / Field : "+
									field.GetName()+"("+field.nPosLine+","+field.nPosCol+") is too long : "								
									+field.nLength) ;
						}
						if (!bFirstForm)
						{
							if (!resStrings.isExistingField(field.nPosLine, field.nPosCol, field.nLength))
							{
								Transcoder.logWarn(0, "Form : "+name+" / Field : "+
										field.GetName()+"("+field.nPosCol+","+field.nPosLine+") does not match fields in other form") ;
							}
						}
						if (bStore)
						{
							ef.AddField(field) ;
							factory.programCatalog.RegisterSymbolicField(field) ;
							String resId = "" ;
							if (!field.csInitialValue.equals(""))
							{
								String name = field.GetName() ;
								if (name.equals(""))
								{
									name = resStrings.CreateName(getName()) ;
									field.SetName(name) ;
								}
								resId = "STR-" + name.toUpperCase() ;
							}
							resStrings.SetResourceText(field.nPosLine, field.nPosCol, field.csInitialValue, getName(), resId, field.nLength) ;
							field.csInitialValue = resId ;
						}
						else // if (!field.csInitialValue.equals(""))
						{
							resStrings.SetResourceText(field.nPosLine, field.nPosCol, field.csInitialValue, getName(), field.nLength) ; // MAP name is used as langid
						}
					}
				}
				else if (le.GetType() == EBMSElementType.GROUP)
				{
					CEntityResourceField field = (CEntityResourceField)le.DoSemanticAnalysis(ef, factory) ;
					field.SetOf(container) ;
					if (bStore)
					{
						ef.AddField(field) ;
						factory.programCatalog.RegisterSymbolicField(field) ;
					}
				}
				else if (le.GetType() == EBMSElementType.ARRAY)
				{
					CEntityResourceField field = (CEntityResourceField)le.DoSemanticAnalysis(ef, factory) ;
					field.SetOf(container) ;
					if (bStore)
					{
						ef.AddField(field) ;
						factory.programCatalog.RegisterSymbolicField(field) ;
					}
				}
				le = (CBMSElement)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
		return ef ;
	}	

	/**
	 * 
	 */
	private class FieldComparator implements Comparator<CFieldElement> 
	{
		public int compare(CFieldElement e1, CFieldElement e2)
		{
			int line1 = e1.posLine ;
			int line2 = e2.posLine ;
			if (line1 < line2)
			{
				return -1 ;
			}
			else if (line1 > line2)
			{
				return 1 ;
			}
			else 
			{
				int col1 = e1.posCol ;
				int col2 = e2.posCol ;
				if (col1 < col2)
				{
					return -1 ;
				}
				else if (col1 > col2)
				{
					return 1 ;
				}
				else
				{
					return 0;
				}
			}
		}
	}
	private void ManageArray()
	{
		LinkedList<CBaseElement> newList = new LinkedList<CBaseElement>() ;
		FieldComparator comp = new FieldComparator() ;
		SortedSet<CFieldElement> setFields =  new TreeSet<CFieldElement>(comp) ;
		ListIterator<CBaseElement> iter = children.listIterator() ;
		try
		{
			CBaseElement o = iter.next() ;
			CFieldElement le = (CFieldElement)o ;
			while (le != null)
			{
				setFields.add(le) ;
				le = (CFieldElement)iter.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
		
		CBMSElement[] arrFields = new CBMSElement[setFields.size()] ;
		setFields.toArray(arrFields) ;
		CFieldArray array = null ;
		for (int i=0; i<arrFields.length; i++)
		{
			CBMSElement le = arrFields[i] ;
			if (array != null)
			{
				if (le.GetType()==EBMSElementType.FIELD)
				{
					CFieldElement field = (CFieldElement)le;
					String s = field.getName() ;
					if (array.ReadField(field))
					{
						//
					}
					else
					{
						array = null ;
						newList.add(le) ;
					}
				}
				else
				{
					newList.add(le) ;
				}
			}
			else
			{
				CFieldElement field = (CFieldElement)le;
				String name = field.getName() ;
				if (name.indexOf('(')>0 && name.indexOf(')')>0)
				{
					String item = name.substring(0, name.indexOf('(')) ;
					String index = name.substring(name.indexOf('(')+1, name.length()-1) ;
					int n = Integer.parseInt(index) ;
					if (n == 1)
					{
						array = new CFieldArray() ;
						newList.add(array) ;
						array.ReadField(field) ;
					}
					else
					{
						throw new NacaTransAssertException("ASSERT ManageArray") ;
					}
				}
				else
				{
					newList.add(le) ;
				}
			}
		}
		children = newList ;
	}

	protected int size_Col = 0 ;
	protected int size_Line = 0 ;
	protected CReservedConstant line = null ;
	protected CReservedConstant column = null ;
	protected CReservedConstant data = null ;
	protected CReservedConstant oBFMT = null ;
	protected CReservedConstant tIOAPFX = null ;
	protected ArrayList<String> arrCTRL = new ArrayList<String>() ;
	protected ArrayList<String> arrMAPATTS = new ArrayList<String>() ;
	protected ArrayList<String> arrDSATTS = new ArrayList<String>() ;
	protected ArrayList<String> arrJUSTIFY = new ArrayList<String>() ;
	protected String trailer = "" ;
	/* (non-Javadoc)
	 * @see parser.CBMSElement#GetType()
	 */
	public EBMSElementType GetType()
	{
		return EBMSElementType.MAP ;
	}

	protected CResourceStrings resStrings = null ;
	public CResourceStrings GetResourceStrings()
	{
		return resStrings;
	}
	public void SetResourceStrings(CResourceStrings res)
	{
		resStrings = res ;
		
	}

	public void setFindArrays()
	{
		bFindArrays = true ;
	}
	protected boolean bFindArrays = false ;
	
	public CBMSElement loadTagParameters(Tag tagCurrent)
	{
		int nLine = tagCurrent.getValAsInt("Line");
		setLine(nLine);
		//line = new CReservedConstant(null, "" + nLine);
		setName(tagCurrent.getVal("Name"));
		
		return loadInternalTags(tagCurrent);
	}

	public CBMSElement parseXMLResource(Tag tag)
	{
		String csName = tag.getName();
		CBMSElement elem = null;
		if(csName.equalsIgnoreCase("Attributes"))
		{
			size_Col = tag.getValAsInt("SizeCol");
			size_Line = tag.getValAsInt("SizeLine");
			
			line = new CReservedConstant(null, tag.getVal("Line"));
			
			column = new CReservedConstant(null, tag.getVal("Column"));
			
			data = new CReservedConstant(null, tag.getVal("Data"));
			
			tIOAPFX = new CReservedConstant(null, tag.getVal("TIOAPFX"));
			
			oBFMT = new CReservedConstant(null, tag.getVal("OBFMT"));
			
			// Enum all sub tags		
			TagCursor curChild = new TagCursor();
			Tag tagChild = tag.getFirstChild(curChild);
			while(tagChild != null)
			{
				String csChildName = tagChild.getName();
				if(csChildName.equalsIgnoreCase("CTRL"))
				{
					String csVal = tagChild.getVal("Value");
					arrCTRL.add(csVal);
				}
				else if(csChildName.equalsIgnoreCase("MAPATTS"))
				{
					String csVal = tagChild.getVal("Value");
					arrMAPATTS.add(csVal);
				}
				else if(csChildName.equalsIgnoreCase("JUSTIFY"))
				{
					String csVal = tagChild.getVal("Value");
					arrJUSTIFY.add(csVal);
				}
				else if(csChildName.equalsIgnoreCase("DSATTS"))
				{
					String csVal = tagChild.getVal("Value");
					arrDSATTS.add(csVal);
				}
				tagChild = tag.getNextChild(curChild);
			}
		}
		else if(csName.equalsIgnoreCase("Field"))
		{
			elem = new CFieldElement("", 0);
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
	
	public void loadFromRES(int nLine, String csName, String csLanguage)
	{
		setLine(nLine);
		if(csLanguage.equalsIgnoreCase("DE"))
			setName(csName + "D");
		else if(csLanguage.equalsIgnoreCase("FR"))
			setName(csName + "F");
		else if(csLanguage.equalsIgnoreCase("IT"))
			setName(csName + "I");
		else if(csLanguage.equalsIgnoreCase("EN"))
			setName(csName + "G");
		else
			setName(csName);
		
		size_Col = 100;
		size_Line = 30;		
		line = new CReservedConstant(null, "NEXT");		
		column = new CReservedConstant(null, "SAME");		
		data = new CReservedConstant(null, "FIELD");		
		tIOAPFX = new CReservedConstant(null, "YES");		
		oBFMT = new CReservedConstant(null, "NO");
		
		arrCTRL.add("HONEOM");
		arrCTRL.add("FREEKB");
		arrMAPATTS.add("COLOR");
		arrMAPATTS.add("PS");
		arrMAPATTS.add("HILIGHT");
		arrMAPATTS.add("VALIDN");
		arrJUSTIFY.add("LEFT");
		arrDSATTS.add("COLOR");
		arrDSATTS.add("PS");
		arrDSATTS.add("HILIGHT");
		arrDSATTS.add("VALIDN");
	}

}
