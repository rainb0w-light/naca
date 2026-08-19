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
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 */
public class CContinue extends CCobolElement
{
    /**
     * @param line
     */
    public CContinue(int line) {
        super(line);
    }
    /* (non-Javadoc)
     * @see parser.CLanguageElement#Parse(lexer.CTokenList)
     */
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetKeyword() != CCobolKeywordList.CONTINUE)
        {
            Transcoder.logError(getLine(), "Expecting 'CONTINUE' keyword") ;
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
        Element e = root.createElement("Continue") ;
        return e ;
    }
    /* (non-Javadoc)
     * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
     */
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
//      CEntityContinue eCont = factory.NewEntityContinue(getLine()) ;
//      parent.AddChild(eCont) ;
//      return eCont ;
        return null ; // the CONTINUE statement does nothing
    }
}
