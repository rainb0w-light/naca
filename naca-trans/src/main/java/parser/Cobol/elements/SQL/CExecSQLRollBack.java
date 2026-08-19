/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements.SQL;

import lexer.CBaseToken;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.SQL.CEntitySQLRollBack;

/**
 * @author U930CV
 *
 */
public class CExecSQLRollBack extends CBaseExecSQLAction
{
    /**
     * @param l
     */
    public CExecSQLRollBack(int l)
    {
        super(l);
    }
    /** Exports the custom. */
    public Element ExportCustom(Document root)
    {
        Element e = root.createElement("SQLRollBack") ;
        return e ;
    }
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntitySQLRollBack eRB = factory.NewEntitySQLRollBack(getLine());
        parent.AddChild(eRB) ;
        return eRB;
    }
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetValue().equals("ROLLBACK"))
        {
            tok = GetNext();
        }
        return true ;
    }
}
