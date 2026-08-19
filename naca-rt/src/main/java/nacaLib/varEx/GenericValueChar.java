/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;


/**
 * @author U930DI
 *
 */
public class GenericValueChar extends GenericValue
{
    GenericValueChar(char c)
    {
        this.c = c;
    }

    String getAsRawString()
    {
        return String.valueOf(c);
    }

    String getAsString()
    {
        return String.valueOf(c);
    }

    int getAsInt()
    {
        return c;
    }

    int getAsUnsignedInt()
    {
        return Math.abs(c);
    }

    Dec getAsDec()
    {
        Dec dec = new Dec(c, "");
        return dec;
    }

    Dec getAsUnsignedDec()
    {
        Dec dec = new Dec(c, "");
        dec.setUnsigned();
        return dec;
    }


    double getAsDouble()
    {
        return c;
    }

    private char c;
}
