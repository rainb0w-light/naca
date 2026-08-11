/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;

import lexer.*;
import parser.Cobol.CCobolElement;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import utils.Transcoder;

import org.w3c.dom.*;


/**
 * @author U930CV
 *
 */
public class CUnparsedToken extends CCobolElement
{
	/**
	 * @param line
	 */
	public CUnparsedToken(int line) {
		super(line);
	}

	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken();
		token = tok.GetValue() ;
		GetNext() ;
		token += ReadStringUntilEOL() ;
		Transcoder.logWarn(tok.getLine(), "Unparsed Token : " + token);
		return true ;
	}

	public Element ExportCustom(Document rootdoc)
	{
		Element e = rootdoc.createElement("UnparsedToken") ;
		e.setAttribute("Token", token) ;
		return e ;
	}

	String token = "" ;

	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		Transcoder.logWarn(getLine(), "No semantic analysis yet for 'UNPARSED TOKEN'") ;
		return null;
	}
}
