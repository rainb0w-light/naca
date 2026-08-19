/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

/**
 * @author U930DI
 *
 */

package nacaLib.program;

import java.util.ArrayList;
import nacaLib.base.CJMapObject;


/** Provides copy replacing behavior. */
public class CopyReplacing extends CJMapObject
{
    private ArrayList<CopyReplacingItem> arr = null;

    /** Creates a new copy replacing instance. */
    public CopyReplacing(int nOldLevel, int nNewLevel)
    {
        arr = new ArrayList<CopyReplacingItem>();
        replacing(nOldLevel, nNewLevel);
    }

    /** Executes the replacing operation. */
    public CopyReplacing replacing(int nOldLevel, int nNewLevel)
    {
        CopyReplacingItem item = new CopyReplacingItem(nOldLevel, nNewLevel);
        arr.add(item);
        return this;
    }

    /** Returns the replaced level. */
    public int getReplacedLevel(int nLevel)
    {
        int nNbItems = arr.size();
        for(int n=0; n<nNbItems; n++)
        {
            CopyReplacingItem item = arr.get(n);
            if (item.nOldLevel == nLevel) {
                return item.nNewLevel;
            }
        }
        return nLevel;
    }
}
