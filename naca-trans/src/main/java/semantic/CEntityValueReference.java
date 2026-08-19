/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CEntityHierarchy;
import utils.CObjectCatalog;

public class CEntityValueReference extends CBaseDataReference
{


    public CEntityValueReference(CObjectCatalog cat, CDataEntity ref)
    {
        super(0, "", cat);
        reference = ref ;
    }

    @Override
    public CDataEntityType GetDataType()
    {
        return reference.GetDataType();
    }

    @Override
    public boolean HasAccessors()
    {
        return reference.HasAccessors();
    }

    @Override
    public boolean isValNeeded()
    {
        return reference.isValNeeded() ;
    }

    @Override
    public String GetConstantValue()
    {
        return reference.GetConstantValue() ;
    }

    public String GetName()
    {
        return reference.GetName() ;
    }

    public String getSemanticReferenceName()
    {
        return reference.GetName();
    }

    @Override
    public CBaseLanguageEntity GetParent()
    {
        return reference.GetParent() ;
    }

    @Override
    public CEntityHierarchy GetHierarchy()
    {
        return reference.GetHierarchy() ;
    }
}
