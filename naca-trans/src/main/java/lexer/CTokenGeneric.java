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
public class CTokenGeneric extends CBaseToken
{
    /** Creates a new ctoken generic instance. */
    public CTokenGeneric(CTokenType type, int line, boolean newline)
    {
        super(line, newline);
        this.type = type ;
        value = type.value ;
    }
    /** Executes the get type operation. */
    public CTokenType GetType()
    {
        return type;
    }
    CTokenType type = null ;
    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    /** Executes the get display operation. */
    public String GetDisplay()
    {
        return type.csSourceValue ;
    }
}
