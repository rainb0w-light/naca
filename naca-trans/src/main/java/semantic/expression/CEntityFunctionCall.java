/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;


import java.util.Vector;


import semantic.CDataEntity;
import utils.CObjectCatalog;


public class CEntityFunctionCall extends CBaseEntityFunction
{
    public CEntityFunctionCall(CObjectCatalog cat, CDataEntity data)
    {
        super(cat, data);
    }
    protected String csFunction = "" ;
    protected Vector<CDataEntity> parameters = new Vector<CDataEntity>() ;

    public void CallFunction(String function)
    {
        csFunction = function ;
    }
    public String getFunctionName()
    {
        return csFunction ;
    }
    @Override
    public boolean isValNeeded()
    {
        return false ;
    }
    public void AddParameter(CDataEntity e)
    {
        parameters.add(e) ;
    }

    public boolean ignore()
    {
        return reference.ignore() ;
    }

}
