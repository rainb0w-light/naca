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
    /** Creates a new ctoken comment instance. */
    public CTokenComment(String comm, int line, boolean newline)
    {
        super(line, newline);
        value = comm ;
    }

    /** Executes the get type operation. */
    public CTokenType GetType()
    {
        return CTokenType.COMMENTS;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return "[COMMENT: " + value + "]" ;
    }

    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    /** Executes the get display operation. */
    public String GetDisplay()
    {
        return "";
    }
}
