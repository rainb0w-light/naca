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
public class VarNumDecSignLeadingComp0 extends VarNum
{
    VarNumDecSignLeadingComp0(DeclareType9 declareType9)
    {
        super(declareType9);
    }

    protected VarNumDecSignLeadingComp0()
    {
        super();
    }

    protected VarBase allocCopy()
    {
        VarNumDecSignLeadingComp0 v = new VarNumDecSignLeadingComp0();
        return v;
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
        double dValue = nValue;
        return compareTo(dValue);
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

    protected byte[] convertUnicodeToEbcdic(char [] tChars)
    {
        return doConvertUnicodeToEbcdic(tChars);
    }

    protected char[] convertEbcdicToUnicode(byte[] tBytes)
    {
        return doConvertEbcdicToUnicode(tBytes);
    }

    public VarType getVarType()
    {
        return VarType.VarNumDecSignLeadingComp0;
    }
}
