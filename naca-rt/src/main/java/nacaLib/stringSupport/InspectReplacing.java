/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 6 d�c. 04
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
// import nacaLib.base.*;
import nacaLib.varEx.CobolConstant;
import nacaLib.varEx.VarAndEdit;

public class InspectReplacing
{	
	public static final InspectReplacingType TypeFirst = new InspectReplacingType();
	public static final InspectReplacingType TypeLeading = new InspectReplacingType();	
	public static final InspectReplacingType TypeAllLowValue = new InspectReplacingType();
	public static final InspectReplacingType TypeAllHighValue = new InspectReplacingType();
	public static final InspectReplacingType TypeAll = new InspectReplacingType();
	public static final InspectReplacingType TypeLeadingSpaces = new InspectReplacingType();
	public static final InspectReplacingType TypeLeadingZeroes = new InspectReplacingType();
	
	public InspectReplacing(VarAndEdit var)
	{
		this.var = var;
	}

	public InspectReplacing before(String csBefore)
	{
		this.csBefore = csBefore;
		return this;
	}
	
	public InspectReplacing before(VarAndEdit varBefore)
	{
		csBefore = varBefore.getString();
		return this;
	}

	public InspectReplacing after(String csAfter)
	{
		this.csAfter = csAfter;
		return this;
	}

	public InspectReplacing after(VarAndEdit varAfter)
	{
		csAfter = varAfter.getString();
		return this;
	}

	
	public InspectReplacing first(String cs)
	{
		inspectReplacingType = TypeFirst;
		csPattern = cs;
		return this;
	}
	
	public InspectReplacing first(VarAndEdit var)
	{
		inspectReplacingType = TypeFirst;
		csPattern = var.getString();
		return this;
	}
	
	public InspectReplacing leading(String cs)
	{
		inspectReplacingType = TypeLeading;
		csPattern = cs;
		return this;
	}
	
	public InspectReplacing allLowValues()
	{
		inspectReplacingType = TypeAllLowValue;
		return this ;
	}
	
	public InspectReplacing allHighValues()
	{
		inspectReplacingType = TypeAllHighValue;
		return this ;
	}
	
	public InspectReplacing all(String s)
	{
		inspectReplacingType = TypeAll;
		csPattern = s ;
		return this ;
	}
	
	public InspectReplacing all(VarAndEdit v)
	{
		inspectReplacingType = TypeAll;
		csPattern = v.getString() ;
		return this ;
	}
	
	public InspectReplacing allSpaces()
	{
		inspectReplacingType = TypeAll;
		csPattern = " " ;
		return this ;
	}

	public InspectReplacing leadingSpaces()
	{
		inspectReplacingType = TypeLeadingSpaces;
		return this ;
	}

	public InspectReplacing leadingZeros()
	{
		inspectReplacingType = TypeLeadingZeroes;
		return this ;
	}
		
	public void bySpaces()
	{
		by(CobolConstant.Space.getValue());
	}

	public void byLowValues()
	{
		by(CobolConstant.LowValue.getValue());
	}

	public void byHighValues()
	{
		by(CobolConstant.HighValue.getValue());
	}

	public void byZero()
	{
		by(CobolConstant.Zero.getValue());
	}
	
	public void by(char c)
	{
		String cs = new String();
		cs += c;
		by(cs);
	}
	
	public void by(VarAndEdit var)
	{
		String cs = var.getString();
		by(cs);
	}
		
	public void by(String csReplacing)
	{
		int nNbCall = 0;
		csSource = var.getString();
		String csPrefixe = null;
		String csSuffixe = null;
		
		// Find substring where to count
		if(csAfter != null)	// We have a starting point
		{
			int nPosAfter = csSource.indexOf(csAfter);
			if(nPosAfter == -1)	// No delimiter found: Nothing to do
				return;
			csPrefixe = csSource.substring(0, nPosAfter+1);
			csSource = csSource.substring(nPosAfter+1);			
		}
		
		if(csBefore != null)	// We have a ending point
		{
			int nPosBefore = csSource.indexOf(csBefore);
			if(nPosBefore == -1)	// No delimiter found: Nothing to do
				return;
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
				char cReplacingChar = csReplacing.charAt(nReplacing);
				csDest.setCharAt(nDest, cReplacingChar);	
			
				nReplacing++;
				if(nReplacing == csReplacing.length())
					nReplacing = 0;
			}
			
			// Find next occurence
			nPos += nReplaceLength;
			int nPosPattern = getReplacePosition(nNbCall, nPos, nReplaceLength);
			if(nPosPattern == -1)
				nPos = -1;
			else
				nPos += nPosPattern;
		}
		
		// Destination string is in csDest
		if(csPrefixe != null || csSuffixe != null)
		{
			String cs = new String(csDest.toString());
			if(csPrefixe != null)
				cs = csPrefixe + cs;
			if(csPrefixe != null)
				cs = cs + csSuffixe;
			var.set(cs);
		}
		else
			var.set(csDest.toString());		
	}
	
	private int getReplacePosition(int nNbCall, int nPosStart, int nNbOccurences)
	{
		String csSource = this.csSource;
		if(nPosStart != 0)
			csSource = csSource.substring(nPosStart);
		int nLg = csSource.length();
		if(inspectReplacingType == TypeFirst)
		{
			if(nNbCall == 0 && nPosStart == 0)	// 1st call
			{
				int nPosPattern = csSource.indexOf(csPattern);	// found the 1st position of the pattern
				if(nPosPattern >= 0)
					return nPosPattern;
			}
		}
		else if(inspectReplacingType == TypeLeading)
		{
			if(nNbCall == 0 && nPosStart == 0)	// 1st call
			{
				int nPosPattern = csSource.indexOf(csPattern);	// found the 1st position of the pattern
				if(nPosPattern >= 0)
					return nPosPattern;
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
					if(nOccurences == nNbOccurences)
						return n;
					n++;
				}
				else	// Retry from this position
				{
					nOccurences = 0;
					n++;
				}		
			}
			if(nOccurences == nNbOccurences)
				return n;
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
					if(nOccurences == nNbOccurences)
						return n;
					n++;
				}
				else	// Retry from this position
				{
					nOccurences = 0;
					n++;
				}		
			}
			if(nOccurences == nNbOccurences)
				return n;
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
			char c = csSource.charAt(0);	// nPosStart);
			if(c == p)
				return 0;
		}
		return -1;
	}

	private int getReplaceLength()
	{
		if(inspectReplacingType == TypeLeadingSpaces)
			return 1;
		if(inspectReplacingType == TypeLeadingZeroes)
			return 1;
		else if(inspectReplacingType == TypeAllLowValue)
			return 1;
		else if(inspectReplacingType == TypeAllHighValue)
			return 1;
		return csPattern.length();
	}
	
	VarAndEdit var = null;
	String csBefore = null;
	String csAfter = null;
	String csSource = null;
	String csPattern = null;
	InspectReplacingType inspectReplacingType = null;
}
