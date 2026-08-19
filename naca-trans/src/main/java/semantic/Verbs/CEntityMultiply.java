/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityMultiply extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntityMultiply(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    protected CDataEntity what = null ;
    protected CDataEntity by = null ;
    protected CDataEntity to = null ;
    protected boolean isisRounded = false ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        what = null ;
        by = null ;
        to = null ;
    }

    /** Sets the multiply. */
    public void SetMultiply(CDataEntity what, CDataEntity by, CDataEntity to, boolean isRounded)
    {
        this.what = what ;
        this.by = by ;
        this.to = to ;
        isisRounded = isRounded ;
    }
    /** Sets the multiply. */
    public void SetMultiply(CDataEntity what, CDataEntity by, boolean isRounded)
    {
        this.what = what ;
        this.by = by ;
        this.to = by ;
        isisRounded = isRounded ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        boolean ignore = what.ignore();
        ignore |= by.ignore();
        ignore |= to.ignore() ;
        return ignore ;
    }

    // ==================== ST4 recursive accessors ====================

    public CDataEntity getValue()
    {
        return what ;
    }

    public CDataEntity getBy()
    {
        return by ;
    }

    public CDataEntity getTo()
    {
        return to ;
    }

    public boolean isRounded()
    {
        return isisRounded ;
    }
}
