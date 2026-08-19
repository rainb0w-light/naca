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
package nacaLib.stringSupport;
// import nacaLib.base.*;
import nacaLib.varEx.CobolConstant;
import nacaLib.varEx.VarAndEdit;

/** Provides inspect replacing behavior. */
public class InspectReplacing
{
    public static final InspectReplacingType TypeFirst = new InspectReplacingType();
    public static final InspectReplacingType TypeLeading = new InspectReplacingType();
    public static final InspectReplacingType TypeAllLowValue = new InspectReplacingType();
    public static final InspectReplacingType TypeAllHighValue = new InspectReplacingType();
    public static final InspectReplacingType TypeAll = new InspectReplacingType();
    public static final InspectReplacingType TypeLeadingSpaces = new InspectReplacingType();
    public static final InspectReplacingType TypeLeadingZeroes = new InspectReplacingType();

    /** Creates a new inspect replacing instance. */
    public InspectReplacing(VarAndEdit var)
    {
        this.var = var;
    }

    /** Executes the before operation. */
    public InspectReplacing before(String csBefore)
    {
        this.csBefore = csBefore;
        return this;
    }

    /** Executes the before operation. */
    public InspectReplacing before(VarAndEdit varBefore)
    {
        csBefore = varBefore.getString();
        return this;
    }

    /** Executes the after operation. */
    public InspectReplacing after(String csAfter)
    {
        this.csAfter = csAfter;
        return this;
    }

    /** Executes the after operation. */
    public InspectReplacing after(VarAndEdit varAfter)
    {
        csAfter = varAfter.getString();
        return this;
    }


    /** Executes the first operation. */
    public InspectReplacing first(String cs)
    {
        inspectReplacingType = TypeFirst;
        csPattern = cs;
        return this;
    }

    /** Executes the first operation. */
    public InspectReplacing first(VarAndEdit var)
    {
        inspectReplacingType = TypeFirst;
        csPattern = var.getString();
        return this;
    }

    /** Executes the first spaces operation. */
    public InspectReplacing firstSpaces()
    {
        return first(" ");
    }

    /** Executes the first zeros operation. */
    public InspectReplacing firstZeros()
    {
        return first("0");
    }

    /** Executes the first low values operation. */
    public InspectReplacing firstLowValues()
    {
        return first(String.valueOf(CobolConstant.LowValue.getValue()));
    }

    /** Executes the first high values operation. */
    public InspectReplacing firstHighValues()
    {
        return first(String.valueOf(CobolConstant.HighValue.getValue()));
    }

    /** Executes the leading operation. */
    public InspectReplacing leading(String cs)
    {
        inspectReplacingType = TypeLeading;
        csPattern = cs;
        return this;
    }

    /** Executes the leading operation. */
    public InspectReplacing leading(VarAndEdit var)
    {
        return leading(var.getString());
    }

    /** Executes the all low values operation. */
    public InspectReplacing allLowValues()
    {
        inspectReplacingType = TypeAllLowValue;
        return this ;
    }

    /** Executes the all high values operation. */
    public InspectReplacing allHighValues()
    {
        inspectReplacingType = TypeAllHighValue;
        return this ;
    }

    /** Executes the all operation. */
    public InspectReplacing all(String s)
    {
        inspectReplacingType = TypeAll;
        csPattern = s ;
        return this ;
    }

    /** Executes the all operation. */
    public InspectReplacing all(VarAndEdit v)
    {
        inspectReplacingType = TypeAll;
        csPattern = v.getString() ;
        return this ;
    }

    /** Executes the all spaces operation. */
    public InspectReplacing allSpaces()
    {
        inspectReplacingType = TypeAll;
        csPattern = " " ;
        return this ;
    }

    /** Executes the all zeros operation. */
    public InspectReplacing allZeros()
    {
        return all("0");
    }

    /** Executes the leading spaces operation. */
    public InspectReplacing leadingSpaces()
    {
        inspectReplacingType = TypeLeadingSpaces;
        return this ;
    }

    /** Executes the leading zeros operation. */
    public InspectReplacing leadingZeros()
    {
        inspectReplacingType = TypeLeadingZeroes;
        return this ;
    }

    /** Executes the leading low values operation. */
    public InspectReplacing leadingLowValues()
    {
        return leading(String.valueOf(CobolConstant.LowValue.getValue()));
    }

    /** Executes the leading high values operation. */
    public InspectReplacing leadingHighValues()
    {
        return leading(String.valueOf(CobolConstant.HighValue.getValue()));
    }

    /** Executes the by spaces operation. */
    public void bySpaces()
    {
        by(CobolConstant.Space.getValue());
    }

    /** Executes the by low values operation. */
    public void byLowValues()
    {
        by(CobolConstant.LowValue.getValue());
    }

    /** Executes the by high values operation. */
    public void byHighValues()
    {
        by(CobolConstant.HighValue.getValue());
    }

    /** Executes the by zero operation. */
    public void byZero()
    {
        by(CobolConstant.Zero.getValue());
    }

    /** Executes the by operation. */
    public void by(char c)
    {
        String cs = new String();
        cs += c;
        by(cs);
    }

    /** Executes the by operation. */
    public void by(VarAndEdit var)
    {
        String cs = var.getString();
        by(cs);
    }

    /** Executes the by operation. */
    public void by(String csReplacing)
    {
        int nNbCall = 0;
        csSource = var.getString();
        String csPrefixe = null;
        String csSuffixe = null;

        // Find substring where to count
        if(csAfter != null) // We have a starting point
        {
            int nPosAfter = csSource.indexOf(csAfter);
            if (nPosAfter == -1) { // No delimiter found: Nothing to do
                return;
            }
            csPrefixe = csSource.substring(0, nPosAfter+1);
            csSource = csSource.substring(nPosAfter+1);
        }

        if(csBefore != null)    // We have a ending point
        {
            int nPosBefore = csSource.indexOf(csBefore);
            if (nPosBefore == -1) {    // No delimiter found: Nothing to do
                return;
            }
            csSuffixe = csSource.substring(nPosBefore);
            csSource = csSource.substring(0, nPosBefore);
        }

        StringBuffer csDest = new StringBuffer(csSource);

        int nReplaceLength = getReplaceLength();
        int nPos = getReplacePosition(nNbCall, 0, nReplaceLength);
        while(nPos != -1)
        {
            nNbCall++;
            // Replace chars
            for(int nDest=nPos, nReplacing=0; nDest<nPos+nReplaceLength; nDest++)
            {
                char replacingChar = csReplacing.charAt(nReplacing);
                csDest.setCharAt(nDest, replacingChar);

                nReplacing++;
                if (nReplacing == csReplacing.length()) {
                    nReplacing = 0;
                }
            }

            // Find next occurence
            nPos += nReplaceLength;
            int nPosPattern = getReplacePosition(nNbCall, nPos, nReplaceLength);
            if (nPosPattern == -1) {
                nPos = -1;
            } else {
                nPos += nPosPattern;
            }
        }

        // Destination string is in csDest
        if(csPrefixe != null || csSuffixe != null)
        {
            String cs = new String(csDest.toString());
            if (csPrefixe != null) {
                cs = csPrefixe + cs;
            }
            if (csPrefixe != null) {
                cs = cs + csSuffixe;
            }
            var.set(cs);
        } else {
            var.set(csDest.toString());
        }
    }

    private int getReplacePosition(int nNbCall, int nPosStart, int nNbOccurences)
    {
        String csSource = this.csSource;
        if (nPosStart != 0) {
            csSource = csSource.substring(nPosStart);
        }
        int nLg = csSource.length();
        if(inspectReplacingType == TypeFirst)
        {
            if(nNbCall == 0 && nPosStart == 0)  // 1st call
            {
                int nPosPattern = csSource.indexOf(csPattern);  // found the 1st position of the pattern
                if (nPosPattern >= 0) {
                    return nPosPattern;
                }
            }
        }
        else if(inspectReplacingType == TypeLeading)
        {
            if(nNbCall == 0 && nPosStart == 0)  // 1st call
            {
                int nPosPattern = csSource.indexOf(csPattern);  // found the 1st position of the pattern
                if (nPosPattern >= 0) {
                    return nPosPattern;
                }
                return -1;
            }
            int nPosPattern = csSource.indexOf(csPattern);
            return nPosPattern;
        }
        else if(inspectReplacingType == TypeAll)
        {
            int nPosPattern = csSource.indexOf(csPattern);
            return nPosPattern;
        }
        else if(inspectReplacingType == TypeAllLowValue)
        {
            // Try to find a consecutive range of nReplaceLength low value chars
            int nOccurences = 0;
            int n = 0;
            while(n != nLg && nOccurences < nNbOccurences)
            {
                char c = csSource.charAt(n);
                if(c == CobolConstant.LowValue.getValue())
                {
                    nOccurences++;
                    if (nOccurences == nNbOccurences) {
                        return n;
                    }
                    n++;
                }
                else    // Retry from this position
                {
                    nOccurences = 0;
                    n++;
                }
            }
            if (nOccurences == nNbOccurences) {
                return n;
            }
            return -1;
        }
        else if(inspectReplacingType == TypeAllHighValue)
        {
            // Try to find a consecutive range of nReplaceLength low value chars
            int nOccurences = 0;
            int n = 0;
            while(n != nLg && nOccurences < nNbOccurences)
            {
                char c = csSource.charAt(n);
                if(c == CobolConstant.HighValue.getValue())
                {
                    nOccurences++;
                    if (nOccurences == nNbOccurences) {
                        return n;
                    }
                    n++;
                }
                else    // Retry from this position
                {
                    nOccurences = 0;
                    n++;
                }
            }
            if (nOccurences == nNbOccurences) {
                return n;
            }
            return -1;
        }
        else if(inspectReplacingType == TypeLeadingSpaces)
        {
            return getReplacePositionLeading(csSource, nLg, ' ');
        }
        else if(inspectReplacingType == TypeLeadingZeroes)
        {
            return getReplacePositionLeading(csSource, nLg, '0');
        }
        return -1;
    }

    private int getReplacePositionLeading(String csSource, int nLg, char p) {
        if(nLg > 0)
        {
            // Try to find all consecutive range of nReplaceLength low value chars
            char c = csSource.charAt(0);    // nPosStart);
            if (c == p) {
                return 0;
            }
        }
        return -1;
    }

    private int getReplaceLength()
    {
        if (inspectReplacingType == TypeLeadingSpaces) {
            return 1;
        }
        if (inspectReplacingType == TypeLeadingZeroes) {
            return 1;
        } else if (inspectReplacingType == TypeAllLowValue) {
            return 1;
        } else if (inspectReplacingType == TypeAllHighValue) {
            return 1;
        }
        return csPattern.length();
    }

    VarAndEdit var = null;
    String csBefore = null;
    String csAfter = null;
    String csSource = null;
    String csPattern = null;
    InspectReplacingType inspectReplacingType = null;
}
