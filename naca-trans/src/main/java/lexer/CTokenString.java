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
public class CTokenString extends CBaseToken
{
    /** Creates a new ctoken string instance. */
    public CTokenString(char[] v, int line, boolean newline)
    {
        super(line, newline);
        charArrayValue =  v ;
        value = new String(v);
    }

    /** Executes the get type operation. */
    public CTokenType GetType()
    {
        return CTokenType.STRING;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return "(\"" + value + "\")" ;
    }

    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    /** Executes the get display operation. */
    public String GetDisplay()
    {
        return "'" + value + "'" ;
    }
    /** Executes the get char value operation. */
    public char[] GetCharValue()
    {
        return charArrayValue ;
    }

    protected char[] charArrayValue = {} ;
}
