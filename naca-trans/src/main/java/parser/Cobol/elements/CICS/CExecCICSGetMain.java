/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements.CICS;

import diagnostic.DiagnosticSink;
import lexer.CBaseToken;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.Cobol.CCobolElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CExecCICSGetMain extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSGetMain(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		DiagnosticSink.recordUnsupported("cics.getmain.runtime-backend-unavailable",
			"embedded-cics", getLine(),
			"EXEC CICS GETMAIN has no supported storage target in NacaRT");
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		//if (tok.GetKeyword() == CCobolKeywordList.)
		{
			tok = GetNext();
		}

		String cs = "" ;
		tok = GetCurrentToken() ;
		while (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			cs += tok.GetDisplay() + " " ;
			tok = GetNext() ;
		}

		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error whle parsing EXEC CICS GETMAIN");
			return false ;
		}
		StepNext();
		return true ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
//		m_Logger.error("No Export for EXEC CICS GETMAIN") ;
		Element e = root.createElement("ExecCICSGetMain") ;
		return e;
	}

}
