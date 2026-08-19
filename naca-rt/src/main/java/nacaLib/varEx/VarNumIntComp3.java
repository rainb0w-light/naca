/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;


/**
 * @author U930DI
 *
 */
public class VarNumIntComp3 extends VarNum
{
    VarNumIntComp3(DeclareType9 declareType9)
    {
        super(declareType9);
    }

    protected VarNumIntComp3()
    {
        super();
    }

    protected VarBase allocCopy()
    {
        VarNumIntComp3 v = new VarNumIntComp3();
        return v;
    }

    /** Executes the debugis storage ascii operation. */
    public boolean DEBUGisStorageAscii()
    {
        return false;
    }

    /** Executes the compare to operation. */
    public int compareTo(ComparisonMode mode, String csValue)
    {
        double dValue = 0;
        try
        {
            dValue = Double.valueOf(csValue).doubleValue();
        }
        catch (Exception ex)
        {
        }
        return compareTo(dValue);
    }

    /** Executes the compare to operation. */
    public int compareTo(int nValue)
    {
        int nVarValue = getInt();
        return nVarValue - nValue;
    }

    /** Executes the compare to operation. */
    public int compareTo(double dValue)
    {
        double varValue = getDouble();
        double d = varValue - dValue;
        if (d < -0.00001) {    //Consider epsilon precision at 10 e-5
            return -1;
        } else if (d > 0.00001) {    //Consider epsilon precision at 10 e-5
            return 1;
        }
        return 0;
    }

    protected byte[] convertUnicodeToEbcdic(char[] tChars)
    {
        return AsciiEbcdicConverter.noConvertUnicodeToEbcdic(tChars);
    }

    protected char[] convertEbcdicToUnicode(byte[] tBytes)
    {
        return AsciiEbcdicConverter.noConvertEbcdicToUnicode(tBytes);
    }

    public VarType getVarType()
    {
        return VarType.VarNumIntComp3;
    }
}
