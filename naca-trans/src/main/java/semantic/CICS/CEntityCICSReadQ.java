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
 * @author sly
 *
 */
public class CEntityCICSReadQ extends CBaseActionEntity
{

    /** Creates a new centity cicsread q instance. */
    public CEntityCICSReadQ(int line, CObjectCatalog cat, boolean bPersistant)
    {
        super(line, cat);
        ispesistant = bPersistant ;
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    protected boolean ispesistant = false ;
    protected CDataEntity queueName = null ;
    protected CDataEntity dataRef = null ;
    protected CDataEntity dataLength = null ;
    protected boolean bReadNext = false ;
    protected CDataEntity numItem = null ;
    protected CDataEntity item = null ;

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        queueName = null ;
        dataRef = null ;
        dataLength = null ;
        numItem = null;
        item = null;
        bReadNext = false;
    }

    /** Sets the name. */
    public void SetName(CDataEntity entity)
    {
        queueName = entity ;
    }
    /** Sets the data ref. */
    public void SetDataRef(CDataEntity entity, CDataEntity len)
    {
        dataRef = entity ;
        dataLength = len ;
    }

    /** Reads the next. */
    public void ReadNext()
    {
        bReadNext = true ;
    }

    /** Reads the num item. */
    public void ReadNumItem(CDataEntity entity)
    {
        numItem = entity ;
    }

    /** Reads the item. */
    public void ReadItem(CDataEntity entity)
    {
        item = entity ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (dataRef == field)
        {
            dataRef = var ;
            field.UnRegisterReadingAction(this) ;
            var.RegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }

    public boolean isPersistent() { return ispesistant; }
    public CDataEntity getQueueName() { return queueName; }
    public CDataEntity getDataRef() { return dataRef; }
    public CDataEntity getDataLength() { return dataLength; }
    public boolean isReadNext() { return bReadNext; }
    public CDataEntity getNumItem() { return numItem; }
    public CDataEntity getItem() { return item; }
}
