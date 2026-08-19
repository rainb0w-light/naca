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
    /** Creates a new ctoken unrecognized instance. */
    public CTokenUnrecognized(char c, int line, boolean newline)
    {
        super(line, newline);
        value = "UNRECOGNIZED: " + c ;
    }
    /** Executes the get type operation. */
    public CTokenType GetType()
    {
        return CTokenType.UNRECOGNIZED;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return "[?" + value + "!]" ;
    }
    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    /** Executes the get display operation. */
    public String GetDisplay()
    {
        return value;
    }
}
