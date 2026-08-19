/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityCurrentDate extends CBaseEntityFunction
{
    /**
     * @param cat
     * @param data
     */
    public CEntityCurrentDate(CObjectCatalog cat)
    {
        super(cat, null);
    }
    public void RegisterReadingAction(CBaseActionEntity act)
    {
        arrActionsReading.add(act) ;
    }
    public void RegisterValueAccess(CBaseEntityCondExpr cond)
    {
        accessAsValue.add(cond) ;
    }
    public void RegisterVarTesting(CBaseEntityCondition cond)
    {
        arrTestsAsVar.add(cond) ;
    }
    public void RegisterWritingAction(CBaseActionEntity act)
    {
        arrActionsWriting.add(act) ;
    }
    public boolean isValNeeded()
    {
        return false;
    }
}
