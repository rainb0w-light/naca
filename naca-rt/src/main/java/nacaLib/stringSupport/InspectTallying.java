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
import nacaLib.varEx.VarAndEdit;

/** Provides inspect tallying behavior. */
public class InspectTallying
{
    public static final InspectTallying TypeForAll = new InspectTallying("");
    public static final InspectTallying TypeForChars = new InspectTallying("");
    public static final InspectTallying TypeLeading = new InspectTallying("");

    /** Creates a new inspect tallying instance. */
    public InspectTallying(VarAndEdit var)
    {
        source = var.getString() ;
    }
    /** Creates a new inspect tallying instance. */
    public InspectTallying(String var)
    {
        this.source = var;
    }


    /** Executes the count all operation. */
    public InspectTallying countAll(String csSearchForAll)
    {
        this.csSearchForAll = csSearchForAll;
        inspectTallyingType = TypeForAll;
        return this;
    }
    /** Executes the count all operation. */
    public InspectTallying countAll(VarAndEdit csSearchForAll, VarAndEdit result)
    {
        return countAll(csSearchForAll.getString(), result);
    }
    /** Executes the count all operation. */
    public InspectTallying countAll(String csSearchForAll, VarAndEdit result)
    {
        this.csSearchForAll = csSearchForAll;
        inspectTallyingType = TypeForAll;
        return to(result);
    }

    /** Executes the count all operation. */
    public InspectTallying countAll(VarAndEdit varSearchForAll)
    {
        csSearchForAll = varSearchForAll.getString();
        inspectTallyingType = TypeForAll;
        return this;
    }

    /** Executes the count leading operation. */
    public InspectTallying countLeading(String csLeading)
    {
        csSearchForAll = csLeading;
        inspectTallyingType = TypeLeading;
        return this;
    }

    /** Executes the count leading operation. */
    public InspectTallying countLeading(VarAndEdit varLeading)
    {
        csSearchForAll = varLeading.getString();
        inspectTallyingType = TypeLeading;
        return this;
    }

    /** Executes the count leading operation. */
    public InspectTallying countLeading(String csLeading, VarAndEdit vto)
    {
        csSearchForAll = csLeading;
        inspectTallyingType = TypeLeading;
        return to(vto) ;
    }

    /** Executes the count chars before operation. */
    public InspectTallying countCharsBefore(VarAndEdit csBefore, VarAndEdit vto)
    {
        return countCharsBefore(csBefore.getString(), vto);
    }

    /** Executes the count chars before operation. */
    public InspectTallying countCharsBefore(String csBefore, VarAndEdit vto) {
        this.csBefore = csBefore;
        inspectTallyingType = TypeForChars;
        return to(vto) ;
    }

    /** Executes the for chars operation. */
    public InspectTallying forChars()
    {
        inspectTallyingType = TypeForChars;
        return this;
    }

    /** Executes the before operation. */
    public InspectTallying before(String csBefore)
    {
        this.csBefore = csBefore;
        return this;
    }

    /** Executes the before operation. */
    public InspectTallying before(VarAndEdit varBefore)
    {
        csBefore = varBefore.getString();
        return this;
    }

    /** Executes the after operation. */
    public InspectTallying after(String csAfter)
    {
        this.csAfter = csAfter;
        return this;
    }

    /** Executes the after operation. */
    public InspectTallying after(VarAndEdit varAfter)
    {
        csAfter = varAfter.getString();
        return this;
    }

    /** Executes the to operation. */
    public InspectTallying to(VarAndEdit varCount)
    {
        String csSource = source ;

        // Find substring where to count
        if(csAfter != null) // We have a starting point
        {
            int nPosAfter = csSource.indexOf(csAfter);
            if (nPosAfter == -1) { // No delimiter found: Nothing to do
                return this;
            }
            csSource = csSource.substring(nPosAfter+1);
        }

        if(csBefore != null)    // We have a ending point
        {
            int nPosBefore = csSource.indexOf(csBefore);
            if (nPosBefore == -1) {    // No delimiter found: Nothing to do
                return this;
            }
            csSource = csSource.substring(0, nPosBefore);
        }

        // We now an the substring on which to operate the counting
        if(inspectTallyingType == TypeForChars) // Count the number of chars
        {
            int nCount = csSource.length();
            varCount.set(nCount+varCount.getInt());
        }
        else if(inspectTallyingType == TypeForAll)  // Count the number of occurences
        {
            int nCount = 0;
            int nPos = csSource.indexOf(csSearchForAll);
            while(nPos >= 0)
            {
                nCount++;
                csSource = csSource.substring(nPos+csSearchForAll.length());
                nPos = csSource.indexOf(csSearchForAll);
            }
            varCount.set(nCount+varCount.getInt());
        }
        // Count the number of occurences, if the string begins by the search pattern
        else if(inspectTallyingType == TypeLeading)
        {
            int nCount = 0;
            int nPos = csSource.indexOf(csSearchForAll);
            if(nPos == 0)   // The source string begins by the search pattern
            {
                while(nPos == 0)
                {
                    nCount++;
                    csSource = csSource.substring(nPos+csSearchForAll.length());
                    nPos = csSource.indexOf(csSearchForAll);
                }
                varCount.set(nCount+varCount.getInt());
            }
        }
        return this ;
    }

    private String source = null;
    private String csSearchForAll = null;
    private String csBefore = null;
    private String csAfter = null;
    private InspectTallying inspectTallyingType = null;
}
