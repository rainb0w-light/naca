/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.calledPrgSupport;

import nacaLib.varEx.CCallParam;
import nacaLib.varEx.Var;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BaseCalledPrgPublicArgPositioned.java,v 1.2 2007/09/21 15:11:30 u930bm Exp $
 */
public abstract class BaseCalledPrgPublicArgPositioned extends CCallParam
{
    private BaseCalledPrgPublicArgWay way = null;

    /** Creates a new base called prg public arg positioned instance. */
    public BaseCalledPrgPublicArgPositioned()
    {
        way = BaseCalledPrgPublicArgWay.IN;
    }

    /** Creates a new base called prg public arg positioned instance. */
    public BaseCalledPrgPublicArgPositioned(boolean bInOut)
    {
        if (bInOut) {
            way = BaseCalledPrgPublicArgWay.INOUT;
        } else {
            way = BaseCalledPrgPublicArgWay.OUT;
        }
    }

    /** Executes the fill with var operation. */
    public void fillWithVar(Var varDest)
    {
        if (way != BaseCalledPrgPublicArgWay.IN) {
            doFillWithVar(varDest);
        }
    }

    /** Executes the do fill with var operation. */
    public abstract void doFillWithVar(Var varDest);
}
