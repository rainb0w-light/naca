/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityReturn extends CBaseActionEntity
{

    /**
     * @param cat
     */
    public CEntityReturn(int l, CObjectCatalog cat)
    {
        super(l, cat);
    }

    /** Sets the stop program. */
    public void SetStopProgram(int returning)
    {
        isstopAllStackCalls = returning;
    }

    /** Sets the only return from procedure. */
    public void SetOnlyReturnFromProcedure()
    {
        bonlyLeaveParagraph = true;
    }

    protected int isstopAllStackCalls = -1 ;
    protected boolean bonlyLeaveParagraph = false ;

    public int getStopAllStackCalls() { return isstopAllStackCalls; }
    public boolean getBonlyLeaveParagraph() { return bonlyLeaveParagraph; }
    /** Target-neutral semantic flag: true when this is a STOP RUN (return-code >= 0). */
    public boolean isStopProgram() { return isstopAllStackCalls >= 0; }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Returns whether s explicit get out. */
    public boolean hasExplicitGetOut()
    {
        return true ;
    }
}
