/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

public class CEntityFileSelect extends CBaseLanguageEntity
{
    public enum AccessMode
    {
        DYNAMIC,
        RANDOM,
        SEQUENTIAL
    }

    public enum OrganizationMode
    {
        INDEXED,
        SEQUENTIAL
    }

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
