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
public class CTokenUnrecognized extends CBaseToken
{
    public CTokenUnrecognized(char c, int line, boolean newline)
    {
        super(line, newline);
        value = "UNRECOGNIZED: " + c ;
    }
    public CTokenType GetType()
    {
        return CTokenType.UNRECOGNIZED;
    }

    public String toString()
    {
        return "[?" + value + "!]" ;
    }
    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    public String GetDisplay()
    {
        return value;
    }
}
