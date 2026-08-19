/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils;

/**
 * @author U930CV
 *
 */
public class NacaTransAssertException extends RuntimeException
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** Creates a new naca trans assert exception instance. */
    public NacaTransAssertException(String cs)
    {
        csMessage = cs ;
    }
    public String csMessage = "" ;
}
