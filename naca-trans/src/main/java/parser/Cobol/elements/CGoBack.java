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

import parser.Cobol.CCobolElement;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.Verbs.CEntityReturn;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 */
public class CGoBack extends CCobolElement
{
    /**
     * @param line
     */
    public CGoBack(int line) {
        super(line);
    }
    /* (non-Javadoc)
     * @see parser.CLanguageElement#Parse(lexer.CTokenList)
     */
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetKeyword() != CCobolKeywordList.GOBACK)
        {
            Transcoder.logError(getLine(), "Expecting 'GOBACK' keyword") ;
            return false ;
        }
        CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
        GetNext();
        return true ;
    }
    /* (non-Javadoc)
     * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
     */
    protected Element ExportCustom(Document root)
    {
        Element e = root.createElement("GoBack") ;
        return e ;
    }
    /* (non-Javadoc)
     * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
     */
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntityReturn e = factory.NewEntityReturn(getLine()) ;
        parent.AddChild(e) ;
        return e ;
    }
}
