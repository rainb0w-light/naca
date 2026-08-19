/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

/**
 * @author PJD
 *
 */
package nacaLib.program;

import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.varEx.VarDeclaration;

/** Provides copy behavior. */
public class Copy
{
    /** Creates a new copy instance. */
    public Copy(BaseProgram program, CopyReplacing copyReplacing)
    {
        declare = new VarDeclaration(program, copyReplacing);
        String csCopyName = toString();
        int n = csCopyName.indexOf('@');
        if (n > 0) {
            csCopyName = csCopyName.substring(0, n);
        }
        if(program != null)
        {
            String csProgramOwnerName = program.getSimpleName();
            CopyManager.register(csCopyName, csProgramOwnerName);
        }
        JmxGeneralStat.incCopyClassLoaded(1);
    }

    /** Executes the finalize operation. */
    public void finalize()
    {
        //Log.logNormal("Copy finalized: " +toString());
        JmxGeneralStat.incCopyClassLoaded(-1);
    }

    protected VarDeclaration declare = null;
}
