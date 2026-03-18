/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.exception;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ApplicativeException.java,v 1.3 2008/02/20 07:53:46 u930gn Exp $
 */
public class ApplicativeException extends Exception
{
	private static final long serialVersionUID = 9203631895766632784L;
	private String csError = null;
	private  String csMessage = null;
	private  Throwable throwable = null;
	
	protected ApplicativeException(ApplicativeException e)
	{
		super(e);
		csError = e.csError;
		csMessage = e.csMessage;
		throwable = e.throwable;
	}
	
	protected ApplicativeException(String csError, String csMessage)
	{
		csError = csError;
		csMessage = csMessage;
		throwable = new Throwable(); 
	}
	
	protected ApplicativeException(String csError, String csMessage, Throwable throwable)
	{
		csError = csError;
		csMessage = csMessage;
		throwable = throwable;
	}
	
	public String getCode()
	{
		return csError;
	}
	
	public String getMessage()
	{
		return csMessage;
	}
	
	public Throwable getThrowable()
	{
		return throwable;
	}
}
