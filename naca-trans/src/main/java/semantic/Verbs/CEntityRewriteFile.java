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

public class CEntityRewriteFile extends CBaseActionEntity
{

    public CEntityRewriteFile(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    public void setFileDescriptor(CEntityFileDescriptor efd, CDataEntity data)
    {
        eFileDescriptor = efd ;
        eDataFrom = data ;
    }
    protected CEntityFileDescriptor eFileDescriptor = null  ;
    protected CDataEntity eDataFrom = null ;

    public CEntityFileDescriptor getFileDescriptor() {
        return eFileDescriptor;
    }
    public CDataEntity getDataFrom() {
        return eDataFrom;
    }
}
