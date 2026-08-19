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
public class GenericValueDec extends GenericValue
{
    GenericValueDec(Dec dec)
    {
        this.dec = dec;
    }

    GenericValueDec(int nInt, String csDec)
    {
        dec = new Dec(nInt, csDec);
    }

    String getAsRawString()
    {
        String cs = dec.getAsString();
        return cs;
    }

    String getAsString()
    {
        return getAsRawString();
    }

    int getAsInt()
    {
        return dec.getSignedInt();
    }

    int getAsUnsignedInt()
    {
        return dec.getUnsignedInt();
    }

    Dec getAsDec()
    {
        return dec;
    }

    Dec getAsUnsignedDec()
    {
        if(dec.isNegative())
        {
            Dec dec = new Dec(this.dec);
            dec.setPositive(true);
            return dec;
        }
        return dec;
    }


    double getAsDouble()
    {
        return dec.getAsDouble();
    }


    private Dec dec = null;
}
