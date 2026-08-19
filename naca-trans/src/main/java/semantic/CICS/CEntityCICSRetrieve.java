/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 */
public class CEntityCICSRetrieve extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSRetrieve(int line,CObjectCatalog cat, boolean ispointer)
    {
        super(line, cat);
        this.ispointer = ispointer;
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    /** Sets the retrieve. */
    public void SetRetrieve(CDataEntity into, CDataEntity length)
    {
        refInto = into ;
        dataLength = length ;
    }

    protected CDataEntity refInto = null;
    protected CDataEntity dataLength = null ;
    protected boolean ispointer = false ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        refInto = null ;
        dataLength = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }
    public CDataEntity getRefInto() { return refInto; }
    public CDataEntity getDataLength() { return dataLength; }
    public boolean isPointer() { return ispointer; }
}
