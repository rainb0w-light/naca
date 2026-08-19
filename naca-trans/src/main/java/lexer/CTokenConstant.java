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
public class CTokenConstant extends CBaseToken
{
    public CTokenConstant(CReservedConstant cste, int line, boolean newline)
    {
        super(line, newline) ;
        this.cste = cste ;
        value = cste.name ;
    }
    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetType()
     */
    public CTokenType GetType()
    {
        return CTokenType.CONSTANT ;
    }
    public CReservedConstant GetConstant()
    {
        return cste ;
    }

    CReservedConstant cste = null ;
    /* (non-Javadoc)
     * @see lexer.CBaseToken#GetDisplay()
     */
    public String GetDisplay()
    {
        return cste.name + " ";
    }
}
