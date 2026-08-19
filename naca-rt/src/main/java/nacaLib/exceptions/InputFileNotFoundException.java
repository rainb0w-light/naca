/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import jlib.misc.LogicalFileDescriptor;

/** Signals a input file not found exception condition. */
public class InputFileNotFoundException extends NacaBatchFileException
{
    private static final long serialVersionUID = 1L;

    /** Creates a new input file not found exception instance. */
    public InputFileNotFoundException(String csFileName, LogicalFileDescriptor logicalFileDescriptor)
    {
        super("InputFileNotFoundException", csFileName, logicalFileDescriptor);
    }
}
