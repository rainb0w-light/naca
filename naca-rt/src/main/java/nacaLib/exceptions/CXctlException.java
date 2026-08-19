/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import nacaLib.basePrgEnv.BaseEnvironment;

/** Signals a cxctl exception condition. */
public class CXctlException extends NacaRTException
{
    private static final long serialVersionUID = 1L;
    public BaseEnvironment environment = null;

    /** Creates a new cxctl exception instance. */
    public CXctlException(BaseEnvironment env)
    {
        environment = env ;
    }
}
