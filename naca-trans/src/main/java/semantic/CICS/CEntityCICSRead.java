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
public class CEntityCICSRead extends CBaseActionEntity
{
    /** Enumerates supported centity cicsread mode values. */
    public enum CEntityCICSReadMode
    {
        NORMAL,
        PREVIOUS,
        NEXT
    }
    /** Creates a new centity cicsread instance. */
    public CEntityCICSRead(int line, CObjectCatalog cat, CEntityCICSReadMode mode)
    {
        super(line, cat);
        this.mode = mode ;
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }
    /** Reads the file. */
    public void ReadFile(CDataEntity name)
    {
        this.name = name ;
        isreadtoDataSet = false ;
        isreadToFile = true ;
        refreshSpecialReadKind();
    }
    /** Reads the data set. */
    public void ReadDataSet(CDataEntity name)
    {
        this.name = name ;
        isreadtoDataSet = true ;
        isreadToFile = false ;
        refreshSpecialReadKind();
    }
    /** Sets the data into. */
    public void SetDataInto(CDataEntity from, CDataEntity length)
    {
        dataInto = from ;
        dataLength = length;
        refreshSpecialReadKind();
    }
    /** Sets the rec idfield. */
    public void SetRecIDField(CDataEntity rec)
    {
        recIDField = rec ;
        refreshSpecialReadKind();
    }

    protected CDataEntity recIDField = null ;
    protected CDataEntity dataInto = null ;
    protected CDataEntity dataLength = null ;
    protected CDataEntity response = null ;
    protected CDataEntity response2 = null ;
    protected CDataEntity name ;
    protected boolean isreadToFile = false ;
    protected boolean isreadtoDataSet = false ;
    protected CEntityCICSReadMode mode = null ;
    protected CDataEntity keyLength = null ;
    protected boolean isequal = false ;
    protected boolean isupdate = false ;
    private SpecialReadKind specialReadKind = SpecialReadKind.NONE;

    private enum SpecialReadKind
    {
        NONE,
        CODE,
        CODE_MEDIA,
        MESSAGE,
        FIELD,
        FIELD_PREVIOUS,
        FIELD_NEXT
    }

    /** Sets the key length. */
    public void SetKeyLength(CDataEntity entity)
    {
        keyLength = entity ;
    }

    /** Sets the equal. */
    public void SetEqual()
    {
        isequal = true ;
    }
    /** Sets the update. */
    public void SetUpdate()
    {
        isupdate = true;
    }

    /** Sets optional RESP targets. */
    public void SetResponses(CDataEntity responseValue, CDataEntity response2Value)
    {
        response = responseValue;
        response2 = response2Value;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        recIDField = null ;
        dataInto = null ;
        dataLength = null ;
        response = null;
        response2 = null;
        name = null ;
        keyLength = null ;
        mode = null ;
        specialReadKind = SpecialReadKind.NONE;
        isreadToFile = false;
        isreadtoDataSet = false;
        isequal = false;
        isupdate = false;
    }

    private void refreshSpecialReadKind()
    {
        specialReadKind = SpecialReadKind.NONE;
        if (dataInto == null || dataInto.of == null)
        {
            return;
        }
        String dataName = dataInto.GetName();
        if (isreadtoDataSet && "PLAU-ZONE".equals(dataName))
        {
            specialReadKind = SpecialReadKind.CODE;
        }
        else if (isreadtoDataSet && "PLAU-ZONE-ASP".equals(dataName))
        {
            specialReadKind = SpecialReadKind.CODE_MEDIA;
        }
        else if (isreadtoDataSet && "MSG-ZONE".equals(dataName))
        {
            specialReadKind = SpecialReadKind.MESSAGE;
        }
        else if ("CURS-ZONE".equals(dataName) && recIDField != null)
        {
            if (mode == CEntityCICSReadMode.PREVIOUS)
            {
                specialReadKind = SpecialReadKind.FIELD_PREVIOUS;
            }
            else if (mode == CEntityCICSReadMode.NEXT)
            {
                specialReadKind = SpecialReadKind.FIELD_NEXT;
            }
            else if (isreadtoDataSet)
            {
                specialReadKind = SpecialReadKind.FIELD;
            }
        }
    }

    public CDataEntity getName() { return name; }
    public CDataEntity getDataInto() { return dataInto; }
    public CDataEntity getDataLength() { return dataLength; }
    public CDataEntity getRecIDField() { return recIDField; }
    public CDataEntity getKeyLength() { return keyLength; }
    public CDataEntity getDataIntoOwner()
    {
        return dataInto == null ? null : dataInto.of;
    }
    public boolean isReadToFile() { return isreadToFile; }
    public boolean isReadToDataSet() { return isreadtoDataSet; }
    public boolean isNormal() { return mode == CEntityCICSReadMode.NORMAL; }
    public boolean isPrevious() { return mode == CEntityCICSReadMode.PREVIOUS; }
    public boolean isNext() { return mode == CEntityCICSReadMode.NEXT; }
    public boolean isEqual() { return isequal; }
    public boolean isUpdate() { return isupdate; }
    public CDataEntity getResponse() { return response; }
    public CDataEntity getResponse2() { return response2; }
    public boolean isReadCode() { return specialReadKind == SpecialReadKind.CODE; }
    public boolean isReadCodeMedia() { return specialReadKind == SpecialReadKind.CODE_MEDIA; }
    public boolean isReadMessage() { return specialReadKind == SpecialReadKind.MESSAGE; }
    public boolean isReadField() { return specialReadKind == SpecialReadKind.FIELD; }
    public boolean isReadFieldPrevious() { return specialReadKind == SpecialReadKind.FIELD_PREVIOUS; }
    public boolean isReadFieldNext() { return specialReadKind == SpecialReadKind.FIELD_NEXT; }
}
