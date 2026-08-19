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
public class CReservedKeyword
{
    public CReservedKeyword(CKeywordList list, String name)
    {
        this.name = name;
        list.Register(this) ;
    }
    public String name ;

    public String toString()
    {
        return name;
    }
}
