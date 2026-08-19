/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

import java.util.ArrayList;

/**
 * @author U930CV
 *
 */
public class CEntityCICSIgnoreCondition extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSIgnoreCondition(int line, CObjectCatalog cat)
    {
        super(line, cat);
        if (cat != null)
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor());
    }
    public void IgnoreCondition(String cond)
    {
        conditions.add(cond);
    }

    protected ArrayList<String> conditions = new ArrayList<String>();
    public java.util.List<String> getConditions()
    {
        return conditions;
    }
    public boolean ignore()
    {
        return false;
    }
}
