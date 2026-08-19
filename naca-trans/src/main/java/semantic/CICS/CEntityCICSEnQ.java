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
public class CEntityCICSEnQ extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntityCICSEnQ(int line, CObjectCatalog cat)
    {
        super(line, cat);
        // The catalog notification is a production-only side effect; the ST4 render
            // tests instantiate this entity directly with a null catalog (like the DEQ
            // and CICS ABEND exemplars), so guard it instead of dereferencing
            // unconditionally.
            if (cat != null)
            {
                cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
            }
    }

    public void SetResource(CDataEntity eRes, CDataEntity eLen)
    {
        resource = eRes ;
        length = eLen ;
    }

    protected CDataEntity resource = null   ;
    protected CDataEntity length = null ;

    public boolean ignore()
    {
        return false ;
    }
    public void Clear()
    {
        super.Clear();
        resource = null ;
        length = null ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveCICSEnQEntity). They expose the already-resolved semantic
    // sub-entities; rendering is done by the template, never here.

    public CDataEntity getResource()
    {
        return resource;
    }

    public CDataEntity getLength()
    {
        return length;
    }
}
