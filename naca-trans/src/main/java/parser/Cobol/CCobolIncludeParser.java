/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol;

import lexer.CTokenList;
import parser.CParser;
import parser.Cobol.elements.CStandAloneWorking;

public class CCobolIncludeParser extends CParser<CStandAloneWorking>
{

	@Override
	protected boolean DoParsing(CTokenList lstTokens)
	{
		CStandAloneWorking working = new CStandAloneWorking(0) ;
		eRoot = working ;
		boolean bParsed = eRoot.Parse(lstTokens, commentContainer) ; 
		return bParsed ;
	}

}
