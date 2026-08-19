/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityEnvironmentVariable;
import semantic.SQL.CEntitySQLCode;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityAssignWithAccessor extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntityAssignWithAccessor(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /** Sets the assign. */
    public void SetAssign(CDataEntity e, CDataEntity val)
    {
        reference = e ;
        value = val ;
    }
    protected CDataEntity reference = null ;
    protected CDataEntity value = null ;
    protected boolean isfillAll = false ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        reference= null ;
        value = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        if (reference == null || value == null)
        {
            return true ;
        }
        return reference.ignore() || value.ignore() ;
    }
    /** Executes the ignore variable operation. */
    public boolean IgnoreVariable(CDataEntity data)
    {
        if (reference == data)
        {
            reference = null ;
            data.UnRegisterWritingAction(this) ;
            return true ;
        }
        else if (value == data)
        {
            value = null ;
            data.UnRegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (reference == field)
        {
            reference = var ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        if (value == field)
        {
            value = var ;
            field.UnRegisterReadingAction(this) ;
            var.RegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }

    /**
     * @param e1
     */
    public void SetValue(CDataEntity e1)
    {
        value = e1 ;
    }

    /**
     * @param e2
     */
    public void SetRefTo(CDataEntity e2)
    {
        reference = e2 ;
    }

    /**
     * @param fillAll
     */
    public void SetFillAll(boolean fillAll)
    {
        isfillAll = fillAll;
    }

    public CDataEntity getReference() { return reference; }
    public CDataEntity getValue() { return value; }
    public boolean isFillAll() { return isfillAll; }
    public boolean isEnvironmentReference()
    {
        return reference instanceof CEntityEnvironmentVariable;
    }
    public CEntityEnvironmentVariable getEnvironmentVariable()
    {
        return isEnvironmentReference()
            ? (CEntityEnvironmentVariable) reference : null;
    }
    /** Returns the environment write accessor. */
    public String getEnvironmentWriteAccessor()
    {
        CEntityEnvironmentVariable environment = getEnvironmentVariable();
        return environment == null ? null : environment.getWriteAccessor();
    }

    public boolean isSQLCodeReference()
    {
        return reference instanceof CEntitySQLCode;
    }
}
