/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

/** Signals a abort session exception condition. */
public class AbortSessionException extends NacaRTException
{
    private static final long serialVersionUID = 1L;

    public Throwable reason = null ;
    public String programName = "" ;

    /** Creates a new abort session exception instance. */
    public AbortSessionException()
    {
        super();
    }

    /** Creates a new abort session exception instance. */
    public AbortSessionException(Throwable e)
    {
        super(e);
    }

    /** Returns the message. */
    public String getMessage()
    {
        String cs = "";
        if (programName != null) {
            cs = "AbortSessionException Prg=" + programName;
        }
        if (reason != null && reason.getMessage() != null) {
            cs += " Reason=" + reason.getMessage();
        }
        return cs;
    }

    /** Returns the reason. */
    public String getReason()
    {
        if(reason != null && reason.getMessage() != null)
        {
            String cs = reason.getMessage();
            return cs;
        }
        return "AbortSessionException Prg=" + programName;
    }
}
