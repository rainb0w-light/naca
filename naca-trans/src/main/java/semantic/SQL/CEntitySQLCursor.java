/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntitySQLCursor extends CDataEntity
{
    /**
     * @param l
     * @param name
     * @param cat
     */
    public CEntitySQLCursor(String name, CObjectCatalog cat)
    {
        super(0, name, cat);
        programCatalog.RegisterSQLCursor(this);
        if (!name.equals(this.GetName()))
        {
            programCatalog.RegisterSQLCursor(name, this);
        }
    }

    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }

    @Override
    public CDataEntityType GetDataType()
    {
        return null;
    }

    @Override
    public boolean isValNeeded()
    {
        return false;
    }

    /** Sets the select. */
    public void SetSelect(CEntitySQLCursorSelectStatement eSQL)
    {
        select = eSQL ;
    }

    protected CEntitySQLCursorSelectStatement select = null ;
    protected CDataEntity variableStatement = null ;

    /** Executes the get nb columns operation. */
    public int GetNbColumns()
    {
        if (select == null) {
            return 0;
        }
        return select.GetNbColumns() ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }

    /**
     *
     */
    public CEntitySQLCursorSelectStatement getSelect()
    {
        return select ;
    }

    /**
     * @param var
     */
    public void setVariableStatement(CDataEntity var)
    {
        variableStatement = var ;
    }
    public CDataEntity getVariableStatement()
    {
        return variableStatement ;
    }

}
