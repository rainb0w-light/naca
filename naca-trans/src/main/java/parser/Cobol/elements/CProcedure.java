/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.CEntityProcedure;
import utils.CGlobalEntityCounter;

/**
 * @author U930CV
 *
 */
public class CProcedure extends CBaseProcedure
{
    /** Creates a new cprocedure instance. */
    public CProcedure(String name, int line)
    {
        super(line);
        csName = name ;
    }

    protected boolean DoParsing()
    {
        CGlobalEntityCounter.GetInstance().CountCobolVerb("PROCEDURE") ;
        return super.DoParsing();
    }
    /* (non-Javadoc)
     * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
     */
    protected Element ExportCustom(Document root)
    {
        Element eProc = root.createElement("Procedure") ;
        eProc.setAttribute("Name", csName) ;
        return eProc;
    }

    protected String csName = "" ;

    /* (non-Javadoc)
     * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
     */
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntityProcedure eProc = factory.NewEntityProcedure(getLine(), csName, parent.getSectionContainer()) ;
        parent.AddChild(eProc) ;
        if (nRewriteLine != 0)
        {
            CGlobalEntityCounter.GetInstance().RegisterProgramToRewrite(parent.GetProgramName(), nRewriteLine, "NEXT SENTENCE") ;
        }
        return eProc;
    }
}
