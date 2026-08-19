/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.misc;

import jlib.log.LogFlow;
import jlib.log.LogFlowStd;

/** Provides log flow display behavior. */
public class LogFlowDisplay extends LogFlow
{
    protected LogFlowDisplay()
    {
        super("LogFlowDisplay");
    }

    /** Returns whether acceptable. */
    public boolean isAcceptable(LogFlow logFlow)
    {
        return this == logFlow || logFlow == LogFlowStd.Any;
    }
}
