/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package lexer;

/**
 * @author U930CV
 *
 */
public class CTokenComment extends CBaseToken
{
	public CTokenComment(String Comm, int line, boolean newline)
	{
		super(line, newline);
		value = Comm ;
	}

	public CTokenType GetType()
	{
		return CTokenType.COMMENTS;
	}
	public String toString()
	{
		return "[COMMENT: " + value + "]" ;
	}

	/* (non-Javadoc)
	 * @see lexer.CBaseToken#GetDisplay()
	 */
	public String GetDisplay()
	{
		return "";
	}
}
