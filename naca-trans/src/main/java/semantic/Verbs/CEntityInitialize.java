/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCode;
import utils.*;

/**
 * @author sly
 *
 */
public class CEntityInitialize extends CBaseActionEntity
{

    /**
     * @param cat
     */
    public CEntityInitialize(int l, CObjectCatalog cat, CDataEntity data)
    {
        super(l, cat);
        this.data = data ;
    }

    protected CDataEntity data = null ;

    protected CDataEntity repNumWith = null ;
    public void ReplaceNumWith(CDataEntity d)
    {
        repNumWith = d ;
    }

    protected CDataEntity repNumEditedWith = null ;
    public void ReplaceNumEditedWith(CDataEntity d)
    {
        repNumEditedWith = d ;
    }

    protected CDataEntity repAlphaWith = null ;
    public void ReplaceAlphaNumWith(CDataEntity d)
    {
        repAlphaWith = d ;
    }

    protected CDataEntity fillAlphaWith = null ;
    public void FillAlphaNumWith(CDataEntity d)
    {
        fillAlphaWith = d ;
    }
    public void Clear()
    {
        super.Clear() ;
        data = null ;
        fillAlphaWith = null ;
        repAlphaWith = null ;
        repNumEditedWith = null ;
        repNumWith = null ;
    }
    public boolean ignore()
    {
        return data == null || data.ignore();
    }

    /* (non-Javadoc)
     * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
     */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (data == field)
        {
            data = var ;
            field.UnRegisterWritingAction(this);
            var.RegisterWritingAction(this) ;
            return true ;
        }
        else if (fillAlphaWith == field)
        {
            fillAlphaWith = var ;
            field.UnRegisterReadingAction(this);
            var.RegisterReadingAction(this) ;
            return true ;
        }
        else if (repAlphaWith == field)
        {
            repAlphaWith = var ;
            field.UnRegisterReadingAction(this);
            var.RegisterReadingAction(this) ;
            return true ;
        }
        else if (repNumEditedWith == field)
        {
            repNumEditedWith = var ;
            field.UnRegisterReadingAction(this);
            var.RegisterReadingAction(this) ;
            return true ;
        }
        else if (repNumWith == field)
        {
            repNumWith = var ;
            field.UnRegisterReadingAction(this);
            var.RegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseActionEntity#IgnoreVariable(semantic.CDataEntity)
     */
    public boolean IgnoreVariable(CDataEntity field)
    {
        if (data == field)
        {
            data = null ;
            field.UnRegisterWritingAction(this);
            return true ;
        }
        else if (fillAlphaWith == field)
        {
            fillAlphaWith = null ;
            field.UnRegisterReadingAction(this);
            return true ;
        }
        else if (repAlphaWith == field)
        {
            repAlphaWith = null ;
            field.UnRegisterReadingAction(this);
            return true ;
        }
        else if (repNumEditedWith == field)
        {
            repNumEditedWith = null ;
            field.UnRegisterReadingAction(this);
            return true ;
        }
        else if (repNumWith == field)
        {
            repNumWith = null ;
            field.UnRegisterReadingAction(this);
            return true ;
        }
        return false ;
    }

    public CDataEntity getData()
    {
        return data;
    }

    public CDataEntity getFillAlphaWith()
    {
        return fillAlphaWith;
    }

    public CDataEntity getRepAlphaWith()
    {
        return repAlphaWith;
    }

    public CDataEntity getRepNumWith()
    {
        return repNumWith;
    }

    public CDataEntity getRepNumEditedWith()
    {
        return repNumEditedWith;
    }

    /** SQLCODE is reset through a dedicated runtime call rather than initialize(). */
    public boolean isSqlCodeReset()
    {
        return data instanceof CEntitySQLCode;
    }

}
