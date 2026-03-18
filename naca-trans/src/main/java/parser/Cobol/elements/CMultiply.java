/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 13 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import lexer.CBaseToken;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityMultiply;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMultiply extends CCobolElement
{

	/**
	 * @param line
	 */
	public CMultiply(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityMultiply eMult = factory.NewEntityMultiply(getLine()) ;
		parent.AddChild(eMult) ;
		CDataEntity eWhat = multiplyWhat.GetDataEntity(getLine(), factory);
		CDataEntity eBy = multiplyBy.GetDataEntity(getLine(), factory);
		if (result != null)
		{
			CDataEntity eTo = result.GetDataReference(getLine(), factory);
			eMult.SetMultiply(eWhat, eBy, eTo, bIsRounded) ;
		}
		else
		{
			eMult.SetMultiply(eWhat, eBy, bIsRounded) ;
		}
		return eMult;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken();
		if (tok.GetKeyword() != CCobolKeywordList.MULTIPLY)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		tok = GetNext();
		multiplyWhat = ReadTerminal();
		
		tok=GetCurrentToken();
		if (tok.GetKeyword() != CCobolKeywordList.BY)
		{
			Transcoder.logError(tok.getLine(), "Unexpecting token : " + tok.GetValue());
			return false ;
		}
		GetNext() ;
		multiplyBy = ReadTerminal();
		
		tok = GetCurrentToken();
		if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
		{
			bIsRounded = true ;
			GetNext() ;
		} 
		else if (tok.GetKeyword() == CCobolKeywordList.GIVING)
		{
			GetNext();
			result = ReadIdentifier();
			tok = GetCurrentToken();
			if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
			{
				bIsRounded = true ;
				GetNext() ;
			} 
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eMult = root.createElement("Multiply");
		Element eWhat = root.createElement("Multiply");
		eMult.appendChild(eWhat) ;
		multiplyWhat.ExportTo(eWhat, root);
		Element eBy = root.createElement("By") ;
		eMult.appendChild(eBy);
		multiplyBy.ExportTo(eBy, root);
		if (result != null)
		{
			Element eResult = root.createElement("To");
			eMult.appendChild(eResult);
			result.ExportTo(eResult, root);			
		}
		return eMult;
	}
	
	protected CTerminal multiplyWhat = null ;
	protected CTerminal multiplyBy = null ;
	protected CIdentifier result = null ;
	protected boolean bIsRounded = false ;
}
