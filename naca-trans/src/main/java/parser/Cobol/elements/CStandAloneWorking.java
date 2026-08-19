/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;

import generate.CJavaEntityFactory;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityExternalDataStructure;

/**
 * @author sly
 *
 */
public class CStandAloneWorking extends CWorking
{

    /**
     * @param line
     */
    public CStandAloneWorking(int line)
    {
        super(line);
    }
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntityExternalDataStructure e = factory.NewEntityExternalDataStructure(getLine(), "") ;
        return e ;
    }
    public CEntityExternalDataStructure DoSemanticAnalysis(CJavaEntityFactory factory)
    {
        return (CEntityExternalDataStructure)DoSemanticAnalysis(null, factory);
    }

}
