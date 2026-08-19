/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

/** Provides centity write file behavior. */
public class CEntityWriteFile extends CBaseActionEntity
{

    /** Creates a new centity write file instance. */
    public CEntityWriteFile(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /** Sets the file descriptor. */
    public void setFileDescriptor(CEntityFileDescriptor efd, CDataEntity data)
    {
        eFileDescriptor = efd ;
        eDataFrom = data ;
    }
    /** Sets the after. */
    public void SetAfter(CDataEntity after)
    {
        this.after = after;
    }
    protected CEntityFileDescriptor eFileDescriptor = null  ;
    protected CDataEntity eDataFrom = null ;
    protected CDataEntity after ;

    public CEntityFileDescriptor getFileDescriptor()
    {
        return eFileDescriptor;
    }

    public CDataEntity getDataFrom()
    {
        return eDataFrom;
    }

    public CDataEntity getAfter()
    {
        return after;
    }

}
