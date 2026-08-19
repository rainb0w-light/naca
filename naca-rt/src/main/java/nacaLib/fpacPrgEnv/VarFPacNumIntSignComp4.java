/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.DeclareTypeFPacSignComp4;
import nacaLib.varEx.VarBuffer;
import nacaLib.varEx.VarBufferPos;
import nacaLib.varEx.VarNumIntSignComp4;

/** Provides var fpac num int sign comp4 behavior. */
public class VarFPacNumIntSignComp4 extends VarNumIntSignComp4
{
    /** Creates a new var fpac num int sign comp4 instance. */
    public VarFPacNumIntSignComp4(DeclareTypeFPacSignComp4 type, VarBuffer varBuffer, int nPosition)
    {
        super(type);
        bufferPos = new VarBufferPos(varBuffer, nPosition);
        varDef.setTotalSize(varDef.getSingleItemRequiredStorageSize());
    }
}
