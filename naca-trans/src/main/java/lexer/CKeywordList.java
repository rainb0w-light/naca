/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package lexer;

import java.util.Hashtable;

/**
 * @author U930CV
 *
 */
public class CKeywordList
{
    protected CKeywordList()
    {
    }

    /** Executes the register operation. */
    public void Register(CReservedKeyword kw)
    {
        tabKeyWords.put(kw.name, kw) ;
    }

    /** Executes the get keyword operation. */
    public CReservedKeyword GetKeyword(String name)
    {
        CReservedKeyword kw = tabKeyWords.get(name);
        return kw ;
    }
    private Hashtable<String, CReservedKeyword> tabKeyWords = new Hashtable<String, CReservedKeyword>() ;


}
