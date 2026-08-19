/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements.SQL;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.SQL.CEntitySQLCloseStatement;
import semantic.SQL.CEntitySQLCursor;




/**
 * @author U930DI
 *
 */
public class CExecSQLClose extends CBaseExecSQLAction
{
    /** Creates a new cexec sqlclose instance. */
    public CExecSQLClose(int l)
    {
        super(l);
    }

    /** Exports the custom. */
    public Element ExportCustom(Document root)
    {
        Element e = root.createElement("SQLCloseCursor") ;
        e.setAttribute("Name", csCursorName);
        return e;
    }

    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntitySQLCursor cur = factory.programCatalog.GetSQLCursor(csCursorName) ;
        if (cur != null)
        {
            CEntitySQLCloseStatement eSQL = factory.NewEntitySQLCloseStatement(getLine(), cur) ;
            parent.AddChild(eSQL) ;
            return eSQL;
        }
        return null ;
    }

    protected boolean DoParsing()
    {
        // Parse until reaching END-EXEC.
        boolean isdone = false ;

        while (!isdone)
        {
            CBaseToken tok = GetCurrentToken() ;
            if (tok.GetType() == CTokenType.IDENTIFIER)
            {
                csCursorName = new String(tok.GetValue());
            }
            if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
            {
                isdone = true ;
                break;
            }
            GetNext();
        }
        return true ;
    }

    private String csCursorName = null;


}
