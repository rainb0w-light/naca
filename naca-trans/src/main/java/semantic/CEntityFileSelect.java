/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/** Provides centity file select behavior. */
public class CEntityFileSelect extends CBaseLanguageEntity
{
    /** Enumerates supported access mode values. */
    public enum AccessMode
    {
        DYNAMIC,
        RANDOM,
        SEQUENTIAL
    }

    /** Enumerates supported organization mode values. */
    public enum OrganizationMode
    {
        INDEXED,
        SEQUENTIAL
    }

    /** Creates a new centity file select instance. */
    public CEntityFileSelect(String name, CObjectCatalog cat)
    {
        super(0, name, cat);
    }

    @Override
    protected void RegisterMySelfToCatalog()
    {
        programCatalog.RegisterFileSelect(this) ;
    }

    public void setOrganizationMode(OrganizationMode eMode)
    {
        eOrganizationMode = eMode ;
    }
    protected OrganizationMode eOrganizationMode = null ;
    public void setAccessMode(AccessMode eMode)
    {
        eAccessmode = eMode ;
    }
    protected AccessMode eAccessmode = null ;
    public void setFileName(CDataEntity fileName)
    {
        csFileName = fileName ;
    }
    protected CDataEntity csFileName ;
    /** Executes the get file name operation. */
    public CDataEntity GetFileName()
    {
        return csFileName ;
    }
    protected CDataEntity fileStatus;
    public void setFileStatus(CDataEntity fileStatus)
    {
        this.fileStatus = fileStatus;
    }
    public CDataEntity getFileStatus()
    {
        return fileStatus;
    }

}
