/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.program;

import java.util.Locale;
import nacaLib.varEx.VarAndEdit;

/** Runtime implementations of COBOL intrinsic functions used by generated programs. */
public final class CobolIntrinsicFunctions
{
    private CobolIntrinsicFunctions()
    {
    }

    /**
     * Returns the one-based ordinal position of the first character in the native collating
     * sequence, matching COBOL FUNCTION ORD semantics.
     */
    public static int ord(String value)
    {
        if (value == null || value.isEmpty())
        {
            throw new IllegalArgumentException("FUNCTION ORD requires a non-empty argument");
        }
        return (value.charAt(0) & 0xff) + 1;
    }

    /** Executes the ord operation. */
    public static int ord(VarAndEdit value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("FUNCTION ORD requires a non-null argument");
        }
        return ord(value.getString());
    }

    /** Implements COBOL FUNCTION UPPER-CASE with locale-independent character mapping. */
    public static String upper_case(String value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("FUNCTION UPPER-CASE requires a non-null argument");
        }
        return value.toUpperCase(Locale.ROOT);
    }

    /** Implements COBOL FUNCTION UPPER-CASE for a NacaRT variable. */
    public static String upper_case(VarAndEdit value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("FUNCTION UPPER-CASE requires a non-null argument");
        }
        return upper_case(value.getString());
    }
}
