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
public class CTokenNumber extends CBaseToken
{
	public CTokenNumber(String v, int line, boolean newline)
	{
		super(line, newline);
		value = v ;
	}

	public CTokenType GetType()
	{
		return CTokenType.NUMBER;
	}

	public String toString()
	{
		return "(" + value + ")" ;
	}

	/* (non-Javadoc)
	 * @see lexer.CBaseToken#GetDisplay()
	 */
	public String GetDisplay()
	{
		return value;
	}
}
