/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;


import semantic.CDataEntity;
import utils.CObjectCatalog;

/** Provides centity address behavior. */
public class CEntityAddress extends CDataEntity
{

    /** Creates a new centity address instance. */
    public CEntityAddress(CObjectCatalog cat, String address)
    {
        super(0, "", cat);
        csAddress = address ;
    }

    /**
     * Pure read-only getter consumed by the recursive ST4 assembler binding
     * {@code addressExpressionEntity}. The address literal is fully resolved while
     * the FPac factory/parser populates this entity; the template only reads this
     * value and never triggers semantic analysis or identifier formatting.
     */
    public String getAddress()
    {
        return csAddress ;
    }

    @Override
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.ADDRESS ;
    }

    protected String csAddress ="" ;

    @Override
    public boolean HasAccessors()
    {
        return false;
    }

    @Override
    public boolean isValNeeded()
    {
        return false;
    }

    @Override
    public String GetConstantValue()
    {
        return csAddress;
    }

}
