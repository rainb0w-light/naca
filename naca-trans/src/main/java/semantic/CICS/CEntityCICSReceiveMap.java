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
public class CEntityCICSReceiveMap extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSReceiveMap(int line, CObjectCatalog cat, CDataEntity name)
    {
        super(line, cat);
        this.name = name ;
        // The catalog notification is a production-only side effect; the ST4 render
        // tests instantiate this entity directly with a null catalog (like the READ
        // and CICS XCTL exemplars), so guard it instead of dereferencing unconditionally.
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    /** Sets the map set. */
    public void SetMapSet(CDataEntity name)
    {
        setName = name ;
    }
    /** Sets the data into. */
    public void SetDataInto(CDataEntity name)
    {
        dataInto = name ;
    }


    protected CDataEntity name = null ;
    protected CDataEntity setName = null ;
    protected CDataEntity dataInto = null ;
    protected CDataEntity response = null ;
    protected CDataEntity response2 = null ;

    /** Sets optional RESP targets. */
    public void SetResponses(CDataEntity responseValue, CDataEntity response2Value)
    {
        response = responseValue;
        response2 = response2Value;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        name = null ;
        setName = null ;
        dataInto = null ;
        response = null;
        response2 = null;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveCICSReceiveMapEntity). They expose the already-resolved semantic
    // sub-entities; rendering is done by the template, never here.

    public CDataEntity getName()
    {
        return name;
    }

    public CDataEntity getSetName()
    {
        return setName;
    }

    public CDataEntity getDataInto()
    {
        return dataInto;
    }

    public CDataEntity getResponse()
    {
        return response;
    }

    public CDataEntity getResponse2()
    {
        return response2;
    }
}
