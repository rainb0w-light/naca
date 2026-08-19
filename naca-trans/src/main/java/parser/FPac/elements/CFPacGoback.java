/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import lexer.CBaseToken;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.FPac.CFPacElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityContinue;

/** Provides cfpac goback behavior. */
public class CFPacGoback extends CFPacElement
{

    /** Creates a new cfpac goback instance. */
    public CFPacGoback(int line)
    {
        super(line);
    }

    @Override
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetKeyword() == CFPacKeywordList.GOBACK)
        {
            tok = GetNext() ;
        }
        return true ;
    }

    @Override
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntityContinue cont = factory.NewEntityContinue(getLine()) ;
        parent.AddChild(cont) ;
        return cont;
    }

    @Override
    protected Element ExportCustom(Document root)
    {
        Element eAdd = root.createElement("GOback") ;
        return eAdd ;
    }

}
