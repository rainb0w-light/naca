/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.map_elements;

import jlib.xml.Tag;
import jlib.xml.TagCursor;
import lexer.*;
import lexer.BMS.CBMSConstantList;
import lexer.BMS.CBMSKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import parser.BMS.CBMSElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.forms.CEntityResourceField;
import semantic.forms.CResourceStrings;
import utils.CEntityHierarchy;
import utils.PosLineCol;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CFieldElement extends CBMSElement
{

	/**
	 * @param name
	 * @param line
	 */
	public CFieldElement(String name, int line)
	{
		super(name, line);
	}

	/* (non-Javadoc)
	 * @see parser.CBMSElement#DoExportCustom(org.w3c.dom.Document)
	 */
	protected Element DoExportCustom(Document root)
	{
		Element eF = root.createElement("Field") ;
		eF.setAttribute("PosLine", String.valueOf(posLine));
		eF.setAttribute("PosCol", String.valueOf(posCol));
		eF.setAttribute("Length", String.valueOf(length));
		if (color != null)
		{
			eF.setAttribute("Color", color.name);
		}
		if (highLight != null)
		{
			eF.setAttribute("HighLight", highLight.name);
		}
		eF.setAttribute("Value", value);
		for (int i=0; i<arrATTRB.size(); i++)
		{
			String val = arrATTRB.get(i) ;
			Element e = root.createElement("ATTRB") ;
			e.setAttribute("Value", val);
			eF.appendChild(e) ; 
		}
		for (int i=0; i<arrJustify.size(); i++)
		{
			String val = arrJustify.get(i) ;
			Element e = root.createElement("JUSTIFY") ;
			e.setAttribute("Value", val);
			eF.appendChild(e) ; 
		}
		return eF ;
	}

	/* (non-Javadoc)
	 * @see parser.CBMSElement#InterpretKeyword(lexer.CReservedKeyword, lexer.CTokenList)
	 */
	protected boolean InterpretKeyword(CReservedKeyword kw, CTokenList lstTokens)
	{
		if (kw == CBMSKeywordList.POS)
		{ //POS=(001,001)
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
			posLine = tok.GetIntValue() ;
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
			posCol = tok.GetIntValue() ;
			tok = GetNext() ;
			if (tok.GetType() != CTokenType.RIGHT_BRACKET)
			{
				Transcoder.logError(getLine(), "Expecting RIGHT_BRACKET") ;
				return false ;
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.LENGTH)
		{ // LENGTH=006
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.NUMBER)
			{
				length = tok.GetIntValue();
			}
			else
			{
				Transcoder.logError(getLine(), "Expecting NUMBER") ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.COLOR)
		{ // COLOR=TURQUOISE
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.TURQUOISE ||
				tok.GetConstant() == CBMSConstantList.GREEN ||
				tok.GetConstant() == CBMSConstantList.YELLOW|| 
				tok.GetConstant() == CBMSConstantList.RED || 
				tok.GetConstant() == CBMSConstantList.PINK || 
				tok.GetConstant() == CBMSConstantList.NEUTRAL || 
				tok.GetConstant() == CBMSConstantList.DEFAULT)
			{
				color = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting COLOR : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.HILIGHT)
		{ // HILIGHT=OFF
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetConstant() == CBMSConstantList.OFF ||
				tok.GetConstant() == CBMSConstantList.REVERSE ||
				tok.GetConstant() == CBMSConstantList.UNDERLINE)
			{
				highLight = tok.GetConstant();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting HIGHLIGHT : " + tok.GetValue()) ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.ATTRB)
		{ // ATTRB=(ASKIP,NORM)
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
				if (tok.GetConstant() == CBMSConstantList.ASKIP ||
					tok.GetConstant() == CBMSConstantList.DRK ||
					tok.GetConstant() == CBMSConstantList.PROT ||
					tok.GetConstant() == CBMSConstantList.UNPROT ||
					tok.GetConstant() == CBMSConstantList.BRT ||
					tok.GetConstant() == CBMSConstantList.NUM ||
					tok.GetConstant() == CBMSConstantList.IC ||
					tok.GetConstant() == CBMSConstantList.FSET ||
					tok.GetConstant() == CBMSConstantList.NORM)
				{
					arrATTRB.add(tok.GetValue()) ;
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
					Transcoder.logError(getLine(), "Unexpecting ATTRIBUTE : " + tok.GetValue()) ;
					return false ; 
				}
				StepNext() ;
			}
		}
		else if (kw == CBMSKeywordList.JUSTIFY)
		{ // JUSTIFY=(LEFT,BLANK)
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
					tok.GetConstant() == CBMSConstantList.RIGHT ||	
					tok.GetConstant() == CBMSConstantList.ZERO ||	
					tok.GetConstant() == CBMSConstantList.BLANK)
				{
					arrJustify.add(tok.GetValue()) ;
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
					Transcoder.logError(getLine(), "Unexpecting JUSTIFY : " + tok.GetValue()) ;
					return false ; 
				}
				StepNext() ;
			}
		}
		else if (kw == CBMSKeywordList.INITIAL)
		{ // 
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.STRING)
			{
				value = tok.GetValue();
			}
			else
			{
				Transcoder.logError(getLine(), "Expecting STRING") ;
				return false ; 
			}
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.GRPNAME)
		{ // 
			CBaseToken tok = GetCurrentToken() ;
			grpName = tok.GetValue() ;
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.PICIN)
		{ // 
			CBaseToken tok = GetCurrentToken() ;
			picIn = tok.GetValue() ;
			StepNext() ;
		}
		else if (kw == CBMSKeywordList.PICOUT)
		{ // 
			CBaseToken tok = GetCurrentToken() ;
			picOut = tok.GetValue() ;
			StepNext() ;
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting keyword : "+ kw.name) ;
			return false ;
		}
		return true ;
	}

	protected int posLine = 0 ;
	protected int posCol = 0 ;
	protected int length = 0 ;
	protected CReservedConstant color = null ;
	protected CReservedConstant highLight = null ;
	protected ArrayList<String> arrATTRB = new ArrayList<String>() ;
	protected ArrayList<String> arrJustify = new ArrayList<String>() ;
	protected String value = "" ;
	protected String grpName = "" ;
	protected String picIn = "" ;
	protected String picOut = "" ;
	protected String csDisplayName = "";

	/* (non-Javadoc)
	 * @see parser.CBMSElement#GetType()
	 */
	public EBMSElementType GetType()
	{
		return EBMSElementType.FIELD ;
	}

	/* (non-Javadoc)
	 * @see parser.CBMSElement#DoSemanticAnalysis(semantic.CBaseEntityFactory)
	 */
	public CBaseLanguageEntity DoSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityResourceField ef ;
		if (getName().equals(""))
		{
			ef = factory.NewEntityLabelField(getLine()) ;
			ef.csInitialValue = value ;
		}
		else
		{
			ef = factory.NewEntityEntryField(getLine(), getName()) ;
			if (!value.equals(""))
			{
				ef.resourceStrings = resourceStrings ;
				ef.csInitialValue = value ;
			}
		}
		ef.SetDisplayName(csDisplayName);
		ef.nPosCol = posCol ;
		ef.nPosLine = posLine ;
		ef.nLength = length ;
		if (highLight != null)
		{
			ef.SetHighLight(highLight.name) ;
		}
		if (color != null)
		{
			ef.SetColor(color.name) ;
		}
		for (int i=0; i<arrATTRB.size(); i++)
		{
			String cs = arrATTRB.get(i) ;
			if (cs.equals("ASKIP"))
			{
				ef.SetProtection("AUTOSKIP") ;
			}
			else if (cs.equals("UNPROT"))
			{
				ef.SetProtection("UNPROTECTED");
			}
			else if (cs.equals("NUM"))
			{
				ef.SetProtection("NUMERIC");
			}
			else if (cs.equals("NORM"))
			{
				ef.SetBrightness("NORMAL");
			}
			else if (cs.equals("DRK"))
			{
				ef.SetBrightness("DARK");
			}
			else if (cs.equals("BRT"))
			{
				ef.SetBrightness("BRIGHT");
			}
			else if (cs.equals("FSET"))
			{
				ef.SetModified();
			}
			else if (cs.equals("IC"))
			{
				ef.SetCursor();
			}
		}
		for (int i=0; i<arrJustify.size(); i++)
		{
			String cs = arrJustify.get(i) ;
			if (cs.equals("LEFT"))
			{
				ef.SetRightJustified(false);
			}
			else if (cs.equals("RIGHT"))
			{
				ef.SetRightJustified(true);
			}
			else if (cs.equals("BLANK"))
			{
				ef.SetFillValue("BLANK");
			}
			else if (cs.equals("ZERO") || cs.equals("ZEROS") || cs.equals("ZEROES"))
			{
				ef.SetFillValue("ZERO");
			}
		}
		return ef;
	}
	public CResourceStrings GetResourceStrings()
	{
		return resourceStrings ;
	}
	public void SetResourceStrings(CResourceStrings res)
	{
		resourceStrings = res ;
	}
	protected CResourceStrings resourceStrings = null ;

	public void SetName(String csAlias)
	{
		setName(csAlias);		
	}

	public String GetGroupName()
	{
		return grpName ;
	}

	public CBMSElement loadTagParameters(Tag tagCurrent)
	{
		int nLine = tagCurrent.getValAsInt("Line");
		setLine(nLine);
		
		setName(tagCurrent.getVal("Name"));

		String csColor = tagCurrent.getVal("Color");
		color = new CReservedConstant(null, csColor); 

		String csHighLight = tagCurrent.getVal("HighLight");
		highLight = new CReservedConstant(null, csHighLight); 

		length = tagCurrent.getValAsInt("Length");
		
		posCol = tagCurrent.getValAsInt("PosCol");
		
		posLine = tagCurrent.getValAsInt("PosLine");
		
		value = tagCurrent.getVal("Value");
		
		// Enum all sub tags		
		TagCursor curChild = new TagCursor();
		Tag tagChild = tagCurrent.getFirstChild(curChild);
		while(tagChild != null)
		{
			String csChildName = tagChild.getName();
			if(csChildName.equalsIgnoreCase("ATTRB"))
			{
				String csVal = tagChild.getVal("Value");
				arrATTRB.add(csVal);
			}
			else if(csChildName.equalsIgnoreCase("JUSTIFY"))
			{
				String csVal = tagChild.getVal("Value");
				arrJustify.add(csVal);
			}
			tagChild = tagCurrent.getNextChild(curChild);
		}
		
		return this;
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
	
	public boolean setAsClosingHBox(PosLineCol posLineCol)
	{
		color = new CReservedConstant(null, "GREEN");
		highLight = new CReservedConstant(null, "OFF");
		setName("");
		setLine(0);
		length = 0;
		posLine = posLineCol.getLine();		
		posCol = posLineCol.getCol() + posLineCol.getLength() + 1;
		posLineCol.setLineColLength(posLine+1, 0, 0);
		if(posCol > 80)
			return false;
		
		
		value = "";
		
		arrATTRB.add("ASKIP");
		arrATTRB.add("NORM");
		return true;
	}
	
	private static int ms_nNbBlank = 0;
	
	private boolean fillFromBlank(PosLineCol posLineCol, Tag tag, String csCurrentLanguage)
	{
		color = new CReservedConstant(null, "GREEN");
		highLight = new CReservedConstant(null, "OFF");
		setName("");
		setLine(ms_nNbBlank++);
		length = 0;	//tag.getValAsInt("length");
		posLine = posLineCol.getLine();		
		posCol = posLineCol.getCol() + posLineCol.getLength() + 1;
		
		value = "";
		
		arrATTRB.add("ASKIP");
		arrATTRB.add("NORM");
		
		return true;
	}
	
	public boolean loadTagParameters(PosLineCol posLineCol, Tag tag, String csCurrentLanguage)
	{
		String csTagName = tag.getName();
		if(csTagName.equalsIgnoreCase("edit"))
		{
			return fillFromEdit(posLineCol, tag, csCurrentLanguage, "");
		}
		else if(csTagName.equalsIgnoreCase("label"))
		{
			boolean b = fillFromEdit(posLineCol, tag, csCurrentLanguage, "");// "_LABEL");
			arrATTRB.add("ASKIP");
			arrATTRB.add("NORM");
			setName("");
			return b;
		}
		else if(csTagName.equalsIgnoreCase("title"))
		{
			boolean b = fillFromEdit(posLineCol, tag, csCurrentLanguage, "");
			while(value.length() < length)
				value = value + " ";							
			arrATTRB.add("ASKIP");
			arrATTRB.add("NORM");
			return b;
		}
		else if(csTagName.equalsIgnoreCase("blank"))
		{
			return fillFromBlank(posLineCol, tag, csCurrentLanguage);
		}
		else if(csTagName.equalsIgnoreCase("switch") && !tag.isValExisting("additem"))
		{
			boolean b = fillFromSwitch(posLineCol, tag, csCurrentLanguage, "");			
			return b;
		}
		return false;
	}
		
		
	private boolean fillFromEdit(PosLineCol posLineCol, Tag tag, String csCurrentLanguage, String csAppendColor)
	{
		String csColor = tag.getVal("color");
		color = new CReservedConstant(null, csColor.toUpperCase() + csAppendColor); 
		
		String csHighLight = tag.getVal("highlighting");
		highLight = new CReservedConstant(null, csHighLight.toUpperCase());
		
		if (tag.isValExisting("namecopy")) {
			setName(tag.getVal("namecopy").toUpperCase().replace('_', '-'));
			csDisplayName = tag.getVal("name").toUpperCase();
		} else if (tag.isValExisting("name"))
			setName(tag.getVal("name").toUpperCase());
		else
			setName("");
		
		//int nSourceLine = tag.getValAsInt("sourceline");
		//setLine(nSourceLine);
		
		length = tag.getValAsInt("length");
		
		posLine = tag.getValAsInt("line");
		
		posCol = tag.getValAsInt("col");
		
		posLineCol.setLineColLength(posLine, posCol, length);

		value = "";
		
		Tag tagTexts = tag.getChild("texts");
		if(tagTexts != null)
		{
			TagCursor curText = new TagCursor();
			Tag tagText = tagTexts.getFirstChild(curText, "text");
			while(tagText != null)
			{
				String csLanguage = tagText.getVal("lang");
				if(csLanguage.equalsIgnoreCase(csCurrentLanguage))
				{
					value = tagText.getText();
					break;
				}
				tagText = tagTexts.getNextChild(curText);
			}
		}
			
		String csJustify = tag.getVal("justify");
		if(csJustify.equalsIgnoreCase("right"))
			arrJustify.add(CBMSConstantList.RIGHT.name) ;
		else if(csJustify.equalsIgnoreCase("left"))
			arrJustify.add(CBMSConstantList.LEFT.name) ;

		String csFill = tag.getVal("fill");
		if(csFill.equalsIgnoreCase("blank"))
			arrJustify.add(CBMSConstantList.BLANK.name) ;
		else if(csFill.equalsIgnoreCase("zero"))
			arrJustify.add(CBMSConstantList.ZERO.name) ;
		
		String csProtection = tag.getVal("protection");
		manageAttrib(csProtection);

		String csIntensity = tag.getVal("intensity");
		manageAttrib(csIntensity);

		boolean bModified = tag.getValAsBoolean("modified");
		if(bModified)
			arrATTRB.add("FSET");
		
		boolean bCursor = tag.getValAsBoolean("cursor");
		if(bCursor)
			arrATTRB.add("IC");
		return true;
	}
	
	private boolean fillFromSwitch(PosLineCol posLineCol, Tag tag, String csCurrentLanguage, String csAppendColor)
	{
		if (tag.isValExisting("name"))				
			setName(tag.getVal("name").toUpperCase());
		else
			setName("");
		
		length = tag.getValAsInt("length");
		
		posLine = tag.getValAsInt("line");
		
		posCol = tag.getValAsInt("col");
		
		posLineCol.setLineColLength(posLine, posCol, length);

		value = "";
		
		Tag tagTexts = tag.getChild("texts");
		if(tagTexts != null)
		{
			TagCursor curText = new TagCursor();
			Tag tagText = tagTexts.getFirstChild(curText, "text");
			while(tagText != null)
			{
				String csLanguage = tagText.getVal("lang");
				if(csLanguage.equalsIgnoreCase(csCurrentLanguage))
				{
					value = tagText.getText();
					break;
				}
				tagText = tagTexts.getNextChild(curText);
			}
		}
			
		arrJustify.add(CBMSConstantList.LEFT.name) ;
		
		arrJustify.add(CBMSConstantList.BLANK.name) ;
		
		arrATTRB.add("ASKIP");
		arrATTRB.add("NORM");
		
		return true;
	}
	
	
	private void manageAttrib(String cs)
	{
		if(cs.equalsIgnoreCase("autoskip"))
			arrATTRB.add("ASKIP");
		else if(cs.equalsIgnoreCase("UNPROTECTED"))
			arrATTRB.add("UNPROT");
		else if(cs.equalsIgnoreCase("NUMERIC"))
		{
			arrATTRB.add("UNPROT");	// correct ?
			arrATTRB.add("NUM");
		}
		else if(cs.equalsIgnoreCase("NORMAL"))
			arrATTRB.add("NORM");
		else if(cs.equalsIgnoreCase("DARK"))
			arrATTRB.add("DRK");
		else if(cs.equalsIgnoreCase("BRIGHT"))
			arrATTRB.add("BRT");
	}
}
