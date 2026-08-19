/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author PJD
 *
 */
public class CobolConstantZero extends CobolConstantBase
{
    public char getValue()
    {
        return '0';
    }

    public String getSTCheckValue()
    {
        return "CobolConstantZero";
    }
}
