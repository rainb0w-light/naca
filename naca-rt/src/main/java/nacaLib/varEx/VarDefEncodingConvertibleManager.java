/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.varEx;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import jlib.misc.ArrayFix;
import jlib.misc.LineRead;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: VarDefEncodingConvertibleManager.java,v 1.13 2007/06/09 12:04:22 u930bm Exp $
 */
public class VarDefEncodingConvertibleManager
{
	public VarDefEncodingConvertibleManager()
	{
	}
	
	public void add(VarDefBase varDefBase)
	{
		int nPosition = varDefBase.DEBUGgetDefaultAbsolutePosition();
		int nLength = varDefBase.getLength();
		int nTraliningLength = varDefBase.getTrailingLengthToNotconvert();
		nLength -= nTraliningLength;
		
		add(nPosition, nLength);
	}
	
	public void add(int nPosition, int nLength)
	{
		add(nPosition, nLength, false);
	}
	public void add(int nPosition, int nLength, boolean bConvertOnlyIfBlank)
	{
		add(nPosition, nLength, bConvertOnlyIfBlank, false);
	}
	public void add(int nPosition, int nLength, boolean bConvertOnlyIfBlank, boolean bConvertPrint)
	{
		if(hash == null)
			hash = new Hashtable<Integer, EncodingConvertionRange>();
		
		EncodingConvertionRange ePrevious = hash.get(nPosition);	// Find the entry whose position preceeds us
		if(ePrevious != null 
				&& ePrevious.isConvertOnlyIfBlank() == bConvertOnlyIfBlank
				&& ePrevious.isConvertPrint() == bConvertPrint)
		{
			hash.remove(nPosition);
			int nLastPos = ePrevious.append(nLength);
			hash.put(nLastPos, ePrevious);
			return;
		}

		EncodingConvertionRange e = new EncodingConvertionRange();
		int nLastPos = e.set(nPosition, nLength);
		e.setConvertOnlyIfBlank(bConvertOnlyIfBlank);
		e.setConvertPrint(bConvertPrint);
		hash.put(nLastPos, e);
	}
	
	public void compress()
	{
		if(arr == null)
		{
			int nSize = hash.size();
			EncodingConvertionRange t[] = new EncodingConvertionRange[nSize];
			
			Collection<EncodingConvertionRange> col = hash.values();
			Iterator<EncodingConvertionRange> iter = col.iterator();
			int n = 0;
			while(iter.hasNext())
			{
				EncodingConvertionRange e = iter.next();
				t[n] = e;
				n++;
			}
			arr = new ArrayFix<EncodingConvertionRange>(t);
			hash = null;
		}
	}

	public void getConvertedBytesAsciiToEbcdic(int nStartPos, byte tbyDest[], int nMaxLengthDest)
	{
		if(arr != null)
		{
			for(int n=0; n<arr.size(); n++)
			{
				EncodingConvertionRange e = arr.get(n);
				e.convertAsciiToEbcdic(tbyDest, nStartPos, nMaxLengthDest);				
			}
		}
	}
	
	public void getConvertedBytesAsciiToEbcdic(LineRead lineRead)
	{
		if(arr != null)
		{
			for(int n=0; n<arr.size(); n++)
			{
				EncodingConvertionRange e = arr.get(n);
				e.convertAsciiToEbcdic(lineRead);				
			}
		}
	}
		
	public void convertEbcdicToAscii(VarBase varDest, int nMaxLengthToConvert)
	{
		int nLastPosToConvert = 0;
		if(arr != null)
		{
			for(int n=0; n<arr.size(); n++)
			{
				EncodingConvertionRange e = arr.get(n);
				if(n == 0)
					nLastPosToConvert = e.getPosition() + nMaxLengthToConvert-1;
				e.convertEbcdicToAscii(varDest, nLastPosToConvert);
			}
		}
	}
	
	public void getConvertedBytesEbcdicToAscii(int nStartPos, byte tbyDest[], int nMaxLengthDest)
	{
		if(arr != null)
		{
			for(int n=0; n<arr.size(); n++)
			{
				EncodingConvertionRange e = arr.get(n);
				e.convertEbcdicToAscii(tbyDest, nStartPos, nMaxLengthDest);				
			}
		}
	}
	
	public void getConvertedBytesEbcdicToAscii(LineRead lineRead)
	{
		if(arr != null)
		{
			for(int n=0; n<arr.size(); n++)
			{
				EncodingConvertionRange e = arr.get(n);
				e.convertEbcdicToAscii(lineRead);				
			}
		}
	}	
	
	public void fillDestAndConvertIntoAscii(LineRead lineRead, VarBase varDest)
	{
		int nLength = varDest.setFromLineRead(lineRead);
		convertEbcdicToAscii(varDest, nLength);
	}

	
	private Hashtable<Integer, EncodingConvertionRange> hash = null;
	private ArrayFix<EncodingConvertionRange> arr = null;
}
