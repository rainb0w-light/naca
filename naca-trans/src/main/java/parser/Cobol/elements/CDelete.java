/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;

import lexer.CBaseToken;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CDelete extends CCobolElement
{

    /**
     * @param line
     */
    public CDelete(int line)
    {
        super(line);
    }
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        Transcoder.logError(getLine(), "No Semantic Analysis for DELETE");
        return null;
    }
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetKeyword() != CCobolKeywordList.DELETE)
        {
            return false ;
        }
        CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;

        tok = GetNext();
        fileDesc = ReadIdentifier();

        tok = GetCurrentToken() ;
        if (tok.GetKeyword() == CCobolKeywordList.RECORD)
        {
            tok = GetNext();
        }
        return true;
    }
    protected Element ExportCustom(Document root)
    {
        Element eDelete = root.createElement("Delete");
        fileDesc.ExportTo(eDelete, root);
        return eDelete ;
    }

    protected CIdentifier fileDesc = null ;
}
