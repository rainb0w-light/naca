/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import jlib.misc.LogicalFileDescriptor;

/** Signals a naca batch file exception condition. */
public class NacaBatchFileException extends NacaRTException
 {
    private static final long serialVersionUID = 1L;
    private String csFileName = null;
    private String csLogicalFileDescriptor = null;
    private String csExceptionName = null;

    /** Creates a new naca batch file exception instance. */
    public NacaBatchFileException(String csExceptionName, String csFileName, LogicalFileDescriptor logicalFileDescriptor)
    {
        this.csExceptionName = csExceptionName;
        this.csFileName = csFileName;
        if (logicalFileDescriptor != null) {
            csLogicalFileDescriptor = logicalFileDescriptor.toString();
        } else {
            csLogicalFileDescriptor = "<EMPTY>";
        }
    }

    /** Returns the message. */
    public String getMessage()
    {
        String cs = csExceptionName + "; LogicalName=" + csFileName + "; PhysicalDescription=" + csLogicalFileDescriptor;
        return cs;
    }
}
