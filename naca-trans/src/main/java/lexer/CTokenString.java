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
    public CTokenString(char[] v, int line, boolean newline)
    {
        super(line, newline);
        charArrayValue =  v ;
        value = new String(v);
    }

    public CTokenType GetType()
    {
        return CTokenType.STRING;
    }
    public String toString()
    {
        return "(\"" + value + "\")" ;
    }

    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    public String GetDisplay()
    {
        return "'" + value + "'" ;
    }
    public char[] GetCharValue()
    {
        return charArrayValue ;
    }

    protected char[] charArrayValue = {} ;
}
