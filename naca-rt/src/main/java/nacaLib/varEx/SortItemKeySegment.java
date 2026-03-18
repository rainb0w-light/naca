/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.log.Asserter;

public class SortItemKeySegment
{
	SortItemKeySegment(boolean bAscending)
	{
		bAscending = bAscending;
	}
	
	void copyChars(char [] tChars, int nStart, int nLength)
	{
		tcKeyValue = new char [nLength];
		for(int n=0; n<nLength; n++)
		{
			tcKeyValue[n] = tChars[n + nStart];
		}
	}
	
	int compare(SortItemKeySegment sortItemKeySegment2)
	{
		Asserter.assertIfFalse(bAscending == sortItemKeySegment2.bAscending);
		Asserter.assertIfFalse(tcKeyValue.length == sortItemKeySegment2.tcKeyValue.length);
		
		int nLength = tcKeyValue.length;
		for(int n=0; n<nLength; n++)
		{
			if(tcKeyValue[n] < sortItemKeySegment2.tcKeyValue[n])
			{
				if(bAscending)
					return -1;
				else
					return 1;
			}
			if(tcKeyValue[n] > sortItemKeySegment2.tcKeyValue[n])
			{
				if(bAscending)
					return 1;
				else
					return -1;
			}
		}
		return 0;
	}
	
	boolean bAscending = true;
	char tcKeyValue[] = null;
}
