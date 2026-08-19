/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.*;

/**
 * @author U930DI
 *
 */
public class GenericValueString extends GenericValue
{
    GenericValueString(String cs)
    {
        this.cs = cs;
    }

    String getAsRawString()
    {
        return cs;
    }

    String getAsString()
    {
        return String.valueOf(cs);
    }


    int getAsInt()
    {
        int n = NumberParser.getAsInt(cs);
        return n;
    }

    int getAsUnsignedInt()
    {
        int n = getAsInt();
        if (n < 0) {
            return -n;
        }
        return n;
    }


    Dec getAsDec()
    {
        long l = NumberParser.getAsLong(cs);
        Dec dec = new Dec(l, "");
        return dec;
    }

    Dec getAsUnsignedDec()
    {
        Dec dec = getAsDec();
        dec.setPositive(true);
        return dec;
    }

    double getAsDouble()
    {
        double d = NumberParser.getAsDouble(cs);
        return d;
    }

    private String cs = null;
}
