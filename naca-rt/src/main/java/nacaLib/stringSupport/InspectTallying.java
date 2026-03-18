/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 7 déc. 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.stringSupport;
import nacaLib.varEx.VarAndEdit;

public class InspectTallying
{
	public static final InspectTallying TypeForAll = new InspectTallying("");
	public static final InspectTallying TypeForChars = new InspectTallying("");
	public static final InspectTallying TypeLeading = new InspectTallying("");

	public InspectTallying(VarAndEdit var)
	{
		source = var.getString() ;
	}
	public InspectTallying(String var)
	{
		source = var;
	}
	
	
	public InspectTallying countAll(String csSearchForAll)
	{
		csSearchForAll = csSearchForAll;
		inspectTallyingType = TypeForAll;
		return this;
	}
	public InspectTallying countAll(VarAndEdit csSearchForAll, VarAndEdit result)
	{
		return countAll(csSearchForAll.getString(), result);
	}
	public InspectTallying countAll(String csSearchForAll, VarAndEdit result)
	{
		csSearchForAll = csSearchForAll;
		inspectTallyingType = TypeForAll;
		return to(result);
	}
	
	public InspectTallying countAll(VarAndEdit varSearchForAll)
	{
		csSearchForAll = varSearchForAll.getString();
		inspectTallyingType = TypeForAll;
		return this;
	}
	
	public InspectTallying countLeading(String csLeading)
	{
		csSearchForAll = csLeading;
		inspectTallyingType = TypeLeading;
		return this;
	}

	public InspectTallying countLeading(VarAndEdit varLeading)
	{
		csSearchForAll = varLeading.getString();
		inspectTallyingType = TypeLeading;
		return this;
	}

	public InspectTallying countLeading(String csLeading, VarAndEdit vto)
	{
		csSearchForAll = csLeading;
		inspectTallyingType = TypeLeading;
		return to(vto) ;
	}

	public InspectTallying countCharsBefore(VarAndEdit csBefore, VarAndEdit vto)
	{
		return countCharsBefore(csBefore.getString(), vto);
	}

	public InspectTallying countCharsBefore(String csBefore, VarAndEdit vto) {
		csBefore = csBefore;
		inspectTallyingType = TypeForChars;
		return to(vto) ;
	}

	public InspectTallying forChars()
	{
		inspectTallyingType = TypeForChars;
		return this;
	}
	
	public InspectTallying before(String csBefore)
	{
		csBefore = csBefore;
		return this;
	}
	
	public InspectTallying before(VarAndEdit varBefore)
	{
		csBefore = varBefore.getString();
		return this;
	}

	public InspectTallying after(String csAfter)
	{
		csAfter = csAfter;
		return this;
	}

	public InspectTallying after(VarAndEdit varAfter)
	{
		csAfter = varAfter.getString();
		return this;
	}
		
	public InspectTallying to(VarAndEdit varCount)
	{
		String csSource = source ;
		
		// Find substring where to count
		if(csAfter != null)	// We have a starting point
		{
			int nPosAfter = csSource.indexOf(csAfter);
			if(nPosAfter == -1)	// No delimiter found: Nothing to do
				return this;
			csSource = csSource.substring(nPosAfter+1);
		}
		
		if(csBefore != null)	// We have a ending point
		{
			int nPosBefore = csSource.indexOf(csBefore);
			if(nPosBefore == -1)	// No delimiter found: Nothing to do
				return this;
			csSource = csSource.substring(0, nPosBefore);
		}
		
		// We now an the substring on which to operate the counting
		if(inspectTallyingType == TypeForChars)	// Count the number of chars
		{
			int nCount = csSource.length();
			varCount.set(nCount+varCount.getInt());
		}
		else if(inspectTallyingType == TypeForAll)	// Count the number of occurences
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
		else if(inspectTallyingType == TypeLeading)	// Count the number of occurences, if the string begins by the search pattern
		{
			int nCount = 0;
			int nPos = csSource.indexOf(csSearchForAll);
			if(nPos == 0)	// The source string begins by the search pattern 
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
