/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

public class AbortSessionException extends NacaRTException
{
	private static final long serialVersionUID = 1L;
	
	public Throwable reason = null ;
	public String programName = "" ;
	
	public AbortSessionException()
	{
		super();
	}

	public AbortSessionException(Throwable e)
	{
		super(e);
	}

	public String getMessage()
	{
		String cs = "";
		if(programName != null)
			cs = "AbortSessionException Prg=" + programName;
		if(reason != null && reason.getMessage() != null)
			cs += " Reason=" + reason.getMessage();
		return cs;		
	}
	
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
