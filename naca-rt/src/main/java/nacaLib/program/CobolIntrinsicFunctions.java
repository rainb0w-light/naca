/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.program;

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
}
