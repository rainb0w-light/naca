/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements.CICS;

import diagnostic.DiagnosticSink;
import lexer.CBaseToken;
import lexer.CReservedKeyword;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CExecCICSReadPrev extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSReadPrev(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (readType == null || fileName == null || dataInto == null)
		{
			DiagnosticSink.recordUnsupported(
				"cics.readprev.missing-required-option",
				"embedded-cics",
				getLine(),
				"EXEC CICS READPREV requires FILE(...) or DATASET(...), plus INTO(...)");
			return null;
		}
		DiagnosticSink.recordUnsupported("cics.readprev.runtime-backend-unavailable",
			"embedded-cics", getLine(),
			"EXEC CICS READPREV requires a configured indexed-file backend");
		return null;
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.READPREV)
		{
			tok = GetNext();
		}

		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.FILE && readType == null)
			{
				readType = CCobolKeywordList.FILE ;
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					fileName = ReadTerminal();
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DATASET && readType == null)
			{
				readType = CCobolKeywordList.DATASET ;
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					fileName = ReadTerminal();
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.INTO)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					dataInto = ReadIdentifier() ;
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("RIDFLD"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					recIDField = ReadIdentifier() ;
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					length = ReadTerminal() ;
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("KEYLENGTH"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					keyLength = ReadTerminal() ;
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else
			{
				isdone = true ;
			}
		}

		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(tok.getLine(), "Error while parsing EXEC CICS READ");
			return false ;
		}
		StepNext();
		return true ;
	}
	protected Element ExportCustom(Document root)
	{
		Element eWr = root.createElement("ExecCICSReadPrevious") ;
		Element e ;
		if (readType == CCobolKeywordList.FILE)
		{
			e = root.createElement("File");
		}
		else if (readType == CCobolKeywordList.DATASET)
		{
			e = root.createElement("Dataset");
		}
		else
		{
			return null ;
		}
		eWr.appendChild(e);
		fileName.ExportTo(e, root);

		if (dataInto != null)
		{
			Element eFrom = root.createElement("Into");
			dataInto.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
		}
		if (keyLength != null)
		{
			Element eFrom = root.createElement("KeyLength");
			keyLength.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
		}
		if (recIDField != null)
		{
			Element eFrom = root.createElement("RecIDField");
			recIDField.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
		}
		return eWr;
	}

	protected CReservedKeyword readType = null ;
	protected CTerminal fileName = null ;
	protected CIdentifier dataInto = null ;
	protected CIdentifier recIDField = null ;
	protected CTerminal dataLength = null ;
	protected CTerminal length = null ;
	protected CTerminal keyLength = null ;

}
