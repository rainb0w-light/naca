/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

public class CEntityFileBuffer extends CDataEntity
{
    protected CEntityFileDescriptor fileDescriptor = null ;

    protected CEntityFileBuffer(String name, CEntityFileDescriptor filedesc, CObjectCatalog cat)
    {
        super(0, name, cat);
        fileDescriptor = filedesc ;
    }

    @Override
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.VAR ;
    }

    @Override
    protected String getSemanticReference()
    {
        return fileDescriptor == null ? GetName() : fileDescriptor.GetName();
    }

    public CEntityFileDescriptor GetFileDescriptor()
    {
        return fileDescriptor ;
    }
    public CEntityFileDescriptor getFileDescriptor()
    {
        return fileDescriptor;
    }

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
        return null;
    }

    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetSubStringReference(semantic.expression.CBaseEntityExpression, semantic.expression.CBaseEntityExpression,
     * semantic.CBaseEntityFactory)
     */
    @Override
    public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
    {
        CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
        ref.SetReference(this, start, length) ;
        return ref ;
    }

    public boolean ignore()
    {
        return false ;
    }

}
