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

import nacaLib.base.CJMapObject;
import nacaLib.varEx.InternalCharBuffer;
import nacaLib.varEx.Var;

/** Provides cesmwrite queue behavior. */
public class CESMWriteQueue extends CJMapObject
{
    protected boolean istransient = false ;
    protected String name = "" ;
    protected CESMQueueManager manager = null;
    protected int nItemPosition = 0 ;

    /** Creates a new cesmwrite queue instance. */
    public CESMWriteQueue(boolean istransient, String name, CESMQueueManager manager)
    {
        this.istransient = istransient;
        this.name = name ;
        this.manager = manager ;
    }

    /** Executes the from operation. */
    public CESMWriteQueue from(Var varSource, Var tsLong)
    {
        return from(varSource, tsLong.getInt());
    }
    /** Executes the from operation. */
    public CESMWriteQueue from(Var varSource, int tsLong)
    {
        if (tsLong > varSource.getLength())
        {
            tsLong = varSource.getLength();
        }
        InternalCharBuffer charBufferCopy = varSource.exportToCharBuffer(tsLong);
        if (manager != null)
        {
            if (isrewrite)
            {
                    manager.writeQueue(istransient, name, charBufferCopy, nItemPosition - 1) ;
            }
            else
            {
                    nItemPosition = manager.writeQueue(istransient, name, charBufferCopy) ;
            }
        }
        return this;
    }

    /** Executes the from operation. */
    public CESMWriteQueue from(Var varSource)
    {
        InternalCharBuffer charBufferCopy = varSource.exportToCharBuffer();
        if (manager != null)
        {
            if (isrewrite)
            {
                    manager.writeQueue(istransient, name, charBufferCopy, nItemPosition - 1) ;
            }
            else
            {
                    nItemPosition = manager.writeQueue(istransient, name, charBufferCopy) ;
            }
        }
        return this;
    }

    /** Executes the item operation. */
    public CESMWriteQueue item(Var tsItem)
    {
        tsItem.set(nItemPosition) ;
        return this ;
    }

    /** Executes the num item operation. */
    public CESMWriteQueue numItem(Var numItem)
    {
        numItem.set(nItemPosition) ;
        return this ;
    }

    /** Executes the main operation. */
    public CESMWriteQueue main()
    {
        return this ;
    }

    /** Executes the auxiliary operation. */
    public CESMWriteQueue auxiliary()
    {
        return this ;
    }

    /** Executes the sys id operation. */
    public CESMWriteQueue sysID(String sysID)
    {
        return this ;
    }

    /** Executes the sys id operation. */
    public CESMWriteQueue sysID(Var sysID)
    {
        return sysID(sysID.getString()) ;
    }

//  public CESMWriteQueue main()
//  {
//      // unsupported
//      return this ;
//  }

    /** Executes the rewrite operation. */
    public CESMWriteQueue rewrite(int item)
    {
        isrewrite = true ;
        nItemPosition = item ;
        return this ;
    }
    protected boolean isrewrite = false ;
    /**
     *
     */
//  public CESMWriteQueue auxiliary()
//  {
//      return this ;
//  }
}
