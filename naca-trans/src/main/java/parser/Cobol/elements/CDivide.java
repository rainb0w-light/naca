/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 13 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityDivide;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDivide extends CCobolElement
{

	/**
	 * @param line
	 */
	public CDivide(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityDivide eDivide = factory.NewEntityDivide(getLine());
		parent.AddChild(eDivide);
		CDataEntity eWhat = divideWhat.GetDataEntity(getLine(), factory);
		eWhat.RegisterReadingAction(eDivide) ;
		CDataEntity eBy = divideBy.GetDataEntity(getLine(), factory);
		eBy.RegisterReadingAction(eDivide) ;
		if (result != null)
		{
			CDataEntity eResult = result.GetDataReference(getLine(), factory) ;
			eResult.RegisterWritingAction(eDivide) ;
			eDivide.SetDivide(eWhat, eBy, eResult, isisRounded);
		}
		else
		{
			eDivide.SetDivide(eWhat, eBy, isisRounded);
		}
		
		if (remainder != null)
		{
			CDataEntity eRem = remainder.GetDataReference(getLine(), factory);
			eRem.RegisterWritingAction(eDivide) ;
			eDivide.SetRemainder(eRem);
		}
		return eDivide;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.DIVIDE)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;

		tok = GetNext();
		divideWhat = ReadTerminal();
		
		tok = GetCurrentToken();
		if (tok.GetKeyword() == CCobolKeywordList.INTO)
		{
			GetNext();
			divideBy = divideWhat ;
			divideWhat = ReadTerminal();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.BY)
		{
			GetNext();
			divideBy = ReadTerminal();
		}
		else
		{
			Transcoder.logError(tok.getLine(), "Unexpecting token : " + tok.GetValue()) ;
			return false ;
		}
		
		tok = GetCurrentToken();
		if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
		{
			isisRounded = true ;
			tok = GetNext();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.GIVING)
		{
			GetNext() ;
			result = ReadIdentifier();
			
			tok = GetCurrentToken();
			if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
			{
				isisRounded = true ;
				tok = GetNext();
			}
			if (tok.GetType() == CTokenType.COMMA)
			{
				tok = GetNext();
			}
			if (tok.GetKeyword() == CCobolKeywordList.REMAINDER)
			{
				GetNext();
				remainder = ReadIdentifier();
			}
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.END_DIVIDE)
		{
			GetNext() ;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eDiv = root.createElement("Divide") ;
		if (isisRounded)
		{
			eDiv.setAttribute("Rounded", "true");
		}
		Element eWhat = root.createElement("Divide") ;
		eDiv.appendChild(eWhat);
		divideWhat.ExportTo(eWhat, root);
		Element eBy = root.createElement("By") ;
		eDiv.appendChild(eBy) ;
		divideBy.ExportTo(eBy, root) ;
		if (result != null)
		{
			Element eTo = root.createElement("To") ;
			eDiv.appendChild(eTo) ;
			result.ExportTo(eTo, root) ;
		}
		if (remainder != null)
		{
			Element eRem = root.createElement("Remainder") ;
			eDiv.appendChild(eRem) ;
			remainder.ExportTo(eRem, root) ;
		}
		return eDiv;
	}

	protected CTerminal divideWhat = null ;
	protected CTerminal divideBy = null ; 
	protected CIdentifier result = null ;
	protected CIdentifier remainder = null ;
	protected boolean isisRounded = false ;
}
