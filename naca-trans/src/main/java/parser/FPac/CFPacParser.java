/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac;

import lexer.CTokenList;
import parser.CParser;
import parser.FPac.elements.CFPacScript;

public class CFPacParser extends CParser<CFPacScript>
{

	@Override
	protected boolean DoParsing(CTokenList lstTokens)
	{
		CFPacScript p = new CFPacScript(0) ;
		eRoot = p ;
		boolean isparsed = p.Parse(lstTokens, commentContainer) ;
		return isparsed;
	}

}
