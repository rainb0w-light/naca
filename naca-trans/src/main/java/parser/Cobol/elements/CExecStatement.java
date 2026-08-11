/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;

import diagnostic.DiagnosticSink;
import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.Cobol.CCobolElement;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 */
public class CExecStatement extends CCobolElement
{
	/**
	 * @param line
	 */
	public CExecStatement(int line) {
		super(line);
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tokNext = GetCurrentToken() ;
		if (tokNext.GetKeyword()!=CCobolKeywordList.EXEC)
		{
			tokNext = GetNext();
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tokNext.GetKeyword().name) ;
		boolean isdone = false ;
		while (!isdone)
		{
			tokNext = GetCurrentToken() ;
			if (tokNext.GetType()==CTokenType.KEYWORD && tokNext.GetKeyword()==CCobolKeywordList.EXEC)
			{
				CCobolElement eExec = new CExecStatement(tokNext.getLine()) ;
				AddChild(eExec) ;
				if (!Parse(eExec))
				{
					Transcoder.logError(getLine(), "Failure while parsing EXEC Statement") ;
					return false ;
				}
			}
			else if (tokNext.GetType()==CTokenType.KEYWORD && tokNext.GetKeyword()==CCobolKeywordList.END_EXEC)
			{
				GetNext();
				isdone = true ;
			}
			else
			{
				csSentence += tokNext.GetDisplay() ;
				GetNext();
			}
			setLine(tokNext.getLine());
		}
//		m_Logger.info("(" +getLine()+ ") EXEC CICS "+csSentence);
		return true;
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eExec = root.createElement("Exec") ;
		eExec.setAttribute("Sentence", csSentence);
		return eExec;
	}
	String csSentence = "" ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		DiagnosticSink.recordUnsupported(
			"cobol.exec.unsupported",
			"cobol-core",
			getLine(),
			"Generic EXEC has no defined Naca runtime semantics");
		return null ;
	}

}
