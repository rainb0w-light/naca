/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import jlib.misc.LogicalFileDescriptor;

/** Signals a cannot open file exception condition. */
public class CannotOpenFileException extends NacaBatchFileException
{
    private static final long serialVersionUID = 1L;

    /** Creates a new cannot open file exception instance. */
    public CannotOpenFileException(String csFileName, LogicalFileDescriptor logicalFileDescriptor)
    {
        super("CannotOpenFileException", csFileName, logicalFileDescriptor);
    }
}
