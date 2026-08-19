/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseEntityFactory;

/**
 * @author sly
 *
 */
public class CEntityRoutineEmulation
{

    /**
     * @param line
     * @param cat
     * @param out
     */
    public CEntityRoutineEmulation(String alias, String display)
    {
        csAlias = alias ;
        csDisplay = display ;
    }

    protected String csDisplay = "" ;
    protected String csAlias = "" ;
    /**
     * @param line
     * @return
     */
    public CEntityRoutineEmulationCall NewCall(int line, CBaseEntityFactory factory)
    {
        CEntityRoutineEmulationCall call = factory.NewEntityRoutineEmulationCall(line) ;
        call.SetDisplay(csDisplay) ;
        return call;
    }
}
