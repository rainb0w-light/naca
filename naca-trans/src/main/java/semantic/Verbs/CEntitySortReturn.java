/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

/** Provides centity sort return behavior. */
public class CEntitySortReturn extends CBaseActionEntity
{

    /** Creates a new centity sort return instance. */
    public CEntitySortReturn(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    protected CEntityFileDescriptor eFileDesc = null ;
    public void setDataReference(CEntityFileDescriptor ref)
    {
        eFileDesc = ref ;
    }
    protected CDataEntity eDataInto = null;
    /** Sets the data reference. */
    public void setDataReference(CEntityFileDescriptor ref, CDataEntity into)
    {
        eDataInto = into ;
        eFileDesc = ref ;
    }

    protected CBaseLanguageEntity blocAtEnd = null ;
    protected CBaseLanguageEntity blocNotAtEnd = null ;

    /** Sets the at end bloc. */
    public void SetAtEndBloc(CBaseLanguageEntity le)
    {
        blocAtEnd = le ;
    }

    /** Sets the not at end bloc. */
    public void SetNotAtEndBloc(CBaseLanguageEntity le)
    {
        blocNotAtEnd = le ;
    }

    public CEntityFileDescriptor getFileDesc() {
        return eFileDesc;
    }
    public CDataEntity getDataInto() {
        return eDataInto;
    }
    public CBaseLanguageEntity getAtEndBloc() {
        return blocAtEnd;
    }
    public CBaseLanguageEntity getNotAtEndBloc() {
        return blocNotAtEnd;
    }
}
