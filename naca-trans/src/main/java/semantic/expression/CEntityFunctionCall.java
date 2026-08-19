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


/** Provides centity function call behavior. */
public class CEntityFunctionCall extends CBaseEntityFunction
{
    /** Creates a new centity function call instance. */
    public CEntityFunctionCall(CObjectCatalog cat, CDataEntity data)
    {
        super(cat, data);
    }
    protected String csFunction = "" ;
    protected Vector<CDataEntity> parameters = new Vector<CDataEntity>() ;

    /** Executes the call function operation. */
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
    /** Adds the parameter. */
    public void AddParameter(CDataEntity e)
    {
        parameters.add(e) ;
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore() ;
    }

}
