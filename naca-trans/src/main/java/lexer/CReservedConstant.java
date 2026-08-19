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
public class CReservedConstant
{
    public CReservedConstant(CConstantList list, String name)
    {
        this.name = name ;
        if (list != null) {
            list.Register(this);
        }
    }
    public String name = "" ;
}
