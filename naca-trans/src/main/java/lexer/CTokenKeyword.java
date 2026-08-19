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
public class CTokenKeyword extends CBaseToken
{
    /** Creates a new ctoken keyword instance. */
    public CTokenKeyword(CReservedKeyword kw, int line, boolean newline)
    {
        super(line, newline);
        this.kw = kw ;
        value = kw.name ;
    }

    /** Executes the get keyword operation. */
    public CReservedKeyword GetKeyword()
    {
        return kw ;
    }

    /** Executes the get type operation. */
    public CTokenType GetType()
    {
        return CTokenType.KEYWORD ;
    }

    CReservedKeyword kw ;

    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    /** Executes the get display operation. */
    public String GetDisplay()
    {
        return kw.name + " ";
    }
}
