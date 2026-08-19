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
public class CTokenIdentifier extends CBaseToken
{
    /** Creates a new ctoken identifier instance. */
    public CTokenIdentifier(String v, int line, boolean newline)
    {
        super(line, newline);
        value = v ;
    }

    /** Executes the get type operation. */
    public CTokenType GetType()
    {
        return CTokenType.IDENTIFIER ;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return "[ID:" + value + "]" ;
    }

    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    /** Executes the get display operation. */
    public String GetDisplay()
    {
        return value + " ";
    }
}
