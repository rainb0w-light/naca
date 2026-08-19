/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.mapSupport;

/** Provides map field flag behavior. */
public class MapFieldFlag
{
    /** Creates a new map field flag instance. */
    public MapFieldFlag()
    {
    }

    /** Executes the duplicate operation. */
    public MapFieldFlag duplicate()
    {
        MapFieldFlag copy = new MapFieldFlag();
        copy.csValue = csValue;
        return copy;
    }

    /** Executes the set operation. */
    public void set(String cs)
    {
        csValue = cs;
    }

    /** Executes the set operation. */
    public void set(char c)
    {
        if (c == 0)
        {
            csValue = null ;
        }
        else
        {
            csValue = new String(Character.toString(c));
        }
    }

    /** Executes the get operation. */
    public String get()
    {
        if (csValue != null)
        {
            return csValue;
        }
        else
        {
            return "" ;
        }
    }

    /** Returns whether flag. */
    public boolean isFlag(String cs)
    {
        if (csValue == null)
        {
            return false ;
        }
        return csValue.equals(cs);
    }

    /** Returns the encoded value. */
    public char getEncodedValue()
    {
        if (csValue != null && csValue.length() >= 1) {
            return csValue.charAt(0);
        }
        return 0;
    }

    /** Sets the encoded value. */
    public void setEncodedValue(char cEncodedValue)
    {
        set(cEncodedValue);
    }

    private String csValue = null;

    /**
     * @return
     */
    public boolean isSet()
    {
        return csValue != null ;
    }

    /**
     *
     */
    public void reset()
    {
        csValue = null ;
    }
}
