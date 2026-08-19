/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Vector;

import parser.expression.CExpression;
import parser.expression.CTerminal;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CIgnoredEntity extends CDataEntity
{

    /**
     * @param l
     * @param name
     * @param cat
     * @param out
     */
    public CIgnoredEntity(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat);
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetDataType()
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.IGNORE ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#HasAccessors()
     */
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#ignore()
     */
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return true ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetSpecialAssignment(semantic.CDataEntity, semantic.CBaseEntityFactory, int)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        return factory.NewEntityNoAction(l) ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetSpecialAssignment(parser.expression.CTerminal, semantic.CBaseEntityFactory, int)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        return factory.NewEntityNoAction(l) ;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetConstantValue()
     */
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
    public boolean isValNeeded()
    {
        return true;
    }


    /** Executes the get array reference operation. */
    public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
    {
        CEntityArrayReference e = factory.NewEntityArrayReference(getLine()) ;
        e.SetReference(this) ;
        for (int i=0; i<v.size(); i++)
        {
            CExpression expr = (CExpression)v.get(i);
            CBaseEntityExpression exp = expr.AnalyseExpression(factory);
            e.AddIndex(exp);
        }
        return e ;
    }

}
