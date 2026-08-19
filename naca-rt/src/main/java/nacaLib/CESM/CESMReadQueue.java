/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930CV
 *
 */

package nacaLib.CESM;

import nacaLib.base.*;
import nacaLib.varEx.Var;

public class CESMReadQueue extends CJMapObject
{
    protected boolean istransient = false ;
    protected String name = "" ;
    protected CESMQueueManager manager = null;
    protected int nRecordPosition = 0;

    public CESMReadQueue(boolean bTransient, String name, CESMQueueManager manager)
    {
        this.istransient = bTransient ;
        this.name = name ;
        this.manager = manager ;
    }

    public CESMReadQueue nextInto(Var tsZone, Var tsLong)
    {
        manager.readNextQueue(istransient, name, tsZone) ;
        return this;
    }

    public CESMReadQueue nextInto(Var tsZone, int length)
    {
        manager.readNextQueue(istransient, name, tsZone);
        return this;
    }

    public CESMReadQueue nextInto(Var tsZone)
    {
        manager.readNextQueue(istransient, name, tsZone) ;
        return this;
    }

    public CESMReadQueue itemInto(int nIndex, Var varItem)
    {
        manager.readIndexedQueue(istransient, name, nIndex, varItem, null) ;
        return this ;
    }

    public CESMReadQueue itemInto(Var varIndex, Var varItem)
    {
        int nIndex = varIndex.getInt();
        manager.readIndexedQueue(istransient, name, nIndex, varItem, null) ;
        return this ;
    }
    public CESMReadQueue itemInto(Var varIndex, Var varItem, Var varLength)
    {
        int nIndex = varIndex.getInt();
        manager.readIndexedQueue(istransient, name, nIndex, varItem, varLength) ;
        return this ;
    }

    public CESMReadQueue itemInto(int index, Var varItem, Var varLength)
    {
        manager.readIndexedQueue(istransient, name, index, varItem, varLength);
        return this;
    }

    public CESMReadQueue numItem(Var varNbItems)    // Get the number of items in the collection
    {
        manager.getNbItems(istransient, name, varNbItems);
        return this ;
    }
}
