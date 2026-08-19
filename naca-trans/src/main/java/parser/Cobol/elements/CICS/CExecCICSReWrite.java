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
public class CExecCICSReWrite extends CCobolElement
{

    /**
     * @param line
     */
    public CExecCICSReWrite(int line)
    {
        super(line);
    }

    /* (non-Javadoc)
     * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
     */
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        if (fileName == null || writeType == null || dataFrom == null)
        {
            DiagnosticSink.recordUnsupported("cics.rewrite.missing-required-option",
                "embedded-cics", getLine(),
                "EXEC CICS REWRITE requires FILE or DATASET, plus FROM");
            return null;
        }
        DiagnosticSink.recordUnsupported("cics.rewrite.runtime-backend-unavailable",
            "embedded-cics", getLine(),
            "EXEC CICS REWRITE requires a configured indexed-file backend");
        return null;
    }

    /* (non-Javadoc)
     * @see parser.CBaseElement#Parse(lexer.CTokenList)
     */
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetKeyword() == CCobolKeywordList.REWRITE)
        {
            tok = GetNext();
        }

        boolean isdone = false ;
        while (!isdone)
        {
            tok = GetCurrentToken() ;
            if (tok.GetKeyword() == CCobolKeywordList.FILE && writeType == null)
            {
                writeType = CCobolKeywordList.FILE ;
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
            else if (tok.GetKeyword() == CCobolKeywordList.DATASET && writeType == null)
            {
                writeType = CCobolKeywordList.DATASET ;
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
            else if (tok.GetKeyword() == CCobolKeywordList.FROM)
            {
                tok = GetNext();
                if (tok.GetType() == CTokenType.LEFT_BRACKET)
                {
                    tok = GetNext();
                    dataFrom = ReadIdentifier() ;
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
                    dataLength = ReadTerminal() ;
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
            Transcoder.logError(tok.getLine(), "Error while parsing EXEC CICS REWRITE");
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
        Element eWr = root.createElement("ExecCICSReWrite") ;
        Element e ;
        if (writeType == CCobolKeywordList.FILE)
        {
            e = root.createElement("File");
        }
        else if (writeType == CCobolKeywordList.DATASET)
        {
            e = root.createElement("Dataset");
        }
        else
        {
            return null ;
        }
        eWr.appendChild(e);
        fileName.ExportTo(e, root);

        if (dataFrom != null)
        {
            Element eFrom = root.createElement("From");
            dataFrom.ExportTo(eFrom, root);
            eWr.appendChild(eFrom);
            if (dataLength != null)
            {
                Element eLen = root.createElement("Length");
                eFrom.appendChild(eLen);
                dataLength.ExportTo(eLen, root);
            }
        }
        return eWr;
    }

    protected CReservedKeyword writeType = null ;
    protected CTerminal fileName = null ;
    protected CIdentifier dataFrom = null ;
    protected CTerminal dataLength = null ;
}
