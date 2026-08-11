/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements.CICS;

import lexer.CBaseToken;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.Cobol.CCobolElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSAskTime;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CExecCICSAskTime extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSAskTime(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSAskTime eCICS = factory.NewEntityCICSAskTime(getLine());
		parent.AddChild(eCICS);
		return eCICS;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ASKTIME)
		{
			tok = GetNext();
		}

		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS ASKTIME");
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
		Element e = root.createElement("ExecCICSAskTime") ;
		return e;
	}

}
