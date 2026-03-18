/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.stringSupport;

import java.util.ArrayList;

import nacaLib.varEx.*;

class UnstringDelimiter
{
	UnstringDelimiter(String cs, boolean bAll)
	{
		cs = cs;
		bAll = bAll;
	}
	
	String getRemaingStringAfterSeparator(String csSource)
	{
		int nStringLength = cs.length();
		if(bAll)
		{
			while(csSource.startsWith(cs))
			{
				csSource = csSource.substring(nStringLength);
			}
		}
		else
		{
			csSource = csSource.substring(nStringLength);
		}
		return csSource;
	}
	
	int removeDelimiterString(String csSource, int nPosStart)
	{
		if(bAll)
		{
			int nLength = cs.length();
			boolean bContinue = true;
			while(bContinue)
			{
				bContinue = false;
				if(csSource.length() >= nPosStart + nLength)
				{
					String csChunk = csSource.substring(nPosStart, nPosStart + nLength);
					if(csChunk.equals(cs))
					{
						nPosStart += nLength;
						bContinue = true;
					}
				}
			}
		}
		else
		{
			int nLength = cs.length();
			nPosStart += nLength; 
		}
		return nPosStart;
	}
	
	String cs = null;
	boolean bAll = false;
}

class UnstringManager
{
	String csCurrentSource;
	ArrayList<UnstringDelimiter> arrDelimiters = new ArrayList<UnstringDelimiter>(); 	// Array of UnstringDelimiter
	boolean bFailed = false; 
	int nCount = 0;
	Var varPointer = null;
	Var varTallying = null;
	int nTallying = 0;
	int nPointer1Based = 1;
	
	
	public UnstringManager(VarAndEdit varSource)
	{
		csCurrentSource = varSource.getString();
	}
	
	public void withPointer(Var varPointer)
	{
		varPointer = varPointer;
	}
	
	public void tallying(Var varTallying)
	{
		varTallying = varTallying;
	}
	
	private boolean checkIfRemainingUnfilledChunks()
	{
		int nPointer0Based = nPointer1Based - 1;	// Must be 0 based
		for(int nDelimiter=0; nDelimiter<arrDelimiters.size(); nDelimiter++)	// Try all delimiters
		{
			UnstringDelimiter delimiter = arrDelimiters.get(nDelimiter);
			int nPositionEndChunk = csCurrentSource.indexOf(delimiter.cs, nPointer0Based);
			if(nPositionEndChunk >= 0)
			{
				return true;
			}
		}
		return false;
	}
	
	void doInto(Var varDelimiterDest, Var varDelimiterIn, Var varCountDest)
	{
		if(!bFailed)
		{
			if(varTallying != null)
				nTallying = varTallying.getInt(); 
			
			UnstringDelimiter delimiterUsed = null;
			
			// find separator to use
			int nPositionEndSepartorUsed = -1;
			if(varPointer != null)
				nPointer1Based = varPointer.getInt();
			
			int nPointer0Based = nPointer1Based - 1;	// Must be 0 based
			if(nPointer0Based < 0 || nPointer0Based >= csCurrentSource.length()) // Check position
			{
				if (nPointer0Based < 0)
					bFailed = true;
				return ;
			}
			
			if(arrDelimiters.isEmpty())
			{
				if (csCurrentSource.length() == 0)
					return;
				varDelimiterDest.set(csCurrentSource);
				int i = Math.min(varDelimiterDest.getLength(),
						csCurrentSource.length() - 1);
				csCurrentSource = csCurrentSource.substring(i);
				return;
			}
			
			for(int nDelimiter=0; nDelimiter<arrDelimiters.size(); nDelimiter++)	// Try all delimiters
			{
				UnstringDelimiter delimiter = arrDelimiters.get(nDelimiter);
				int nPositionEndChunk = csCurrentSource.indexOf(delimiter.cs, nPointer0Based);
				if(nPositionEndChunk >= 0 && (nPositionEndChunk < nPositionEndSepartorUsed || nPositionEndSepartorUsed == -1))
				{
					nPositionEndSepartorUsed = nPositionEndChunk;
					delimiterUsed = delimiter;
				}
			}
			
			if(delimiterUsed != null)	// Found 1st delimitered string
			{
				String csChunk = csCurrentSource.substring(nPointer0Based, nPositionEndSepartorUsed);
				fillChunk(csChunk, varDelimiterDest, varCountDest, varDelimiterIn, delimiterUsed.cs);
				incTallyingCount();
				nPointer0Based = delimiterUsed.removeDelimiterString(csCurrentSource, nPositionEndSepartorUsed);	// Remove delimiter string, optionnally managing all occurences

				fillOutPointer(nPointer0Based);
				
				return;
				
//				csCurrentSource = csCurrentSource.substring(nPosSep);
//				csCurrentSource = delimiterUsed.getRemaingStringAfterSeparator(csCurrentSource);	// Keep only right part, after all separators
			}
			
			// Maybe sone source chars remains
			String csLastChunkOnRight = csCurrentSource.substring(nPointer0Based);
			fillChunk(csLastChunkOnRight, varDelimiterDest, varCountDest, varDelimiterIn, "");
			incTallyingCount();
			nPointer0Based += csLastChunkOnRight.length(); 
			fillOutPointer(nPointer0Based);
			return ;
		}
		
		// Not found substring
		if(varPointer != null)
		{
			int nPointer = csCurrentSource.length() +1;	// Points after the source string's last char (1 based)
			varPointer.set(nPointer);
		}
	
		if(varDelimiterDest != null)
			varDelimiterDest.set("");
		
		if(varDelimiterIn != null)
			varDelimiterIn.set("");
		
		if(varCountDest != null)
			varCountDest.set(0);
	}
	
	private void fillOutPointer(int nPointer0Based)
	{
		nPointer1Based = nPointer0Based + 1;	// Must be 1 based on output
		if(varPointer != null)
			varPointer.set(nPointer1Based);
	}
	
	
	private void fillChunk(String csChunk, Var varDelimiterDest, Var varCountDest, Var varDelimiterIn, String csDelimiterUsed)
	{		
		nCount = csChunk.length();
		if(varCountDest != null)
			varCountDest.set(nCount);

		if(varDelimiterIn != null)
			varDelimiterIn.set(csDelimiterUsed);
		
		if(varDelimiterDest != null)
			varDelimiterDest.set(csChunk);
	}
	
	private void incTallyingCount()
	{
		nTallying++;
		if(varTallying != null)					
			varTallying.set(nTallying);
	}
	
	boolean failed()
	{
		if(bFailed)
			return bFailed;
		return checkIfRemainingUnfilledChunks();	// If we have some chunks left that have not been conummed by into calls(), then we have an error 		
	}
}
