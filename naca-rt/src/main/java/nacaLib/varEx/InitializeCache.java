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

import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class InitializeCache
{
	public InitializeCache()
	{
		arr = new ArrayDyn<InitializeCachedItem>();
	}
	
	void setFilledAndcompress(int nBaseAbsolutePosition)
	{
		isfilled = true;
		this.nBaseAbsolutePosition = nBaseAbsolutePosition;

		if(arr != null)
		{	
			// Swap the type inside arr
			if(arr.isDyn())
			{
				int nSize = arr.size();
				InitializeCachedItem targetArr[] = new InitializeCachedItem[nSize];
				arr.transferInto(targetArr);

				ArrayFix<InitializeCachedItem> initializeCachedItemFix = new ArrayFix<InitializeCachedItem>(targetArr);
				arr = initializeCachedItemFix;	// replace by a fix one (uning less memory)
			}
		}
	}
	
	void setNotManaged()
	{
		ismanaged = false;
	}
	
	boolean isFilled()
	{
		return isfilled;
	}
	
	public boolean isManaged()
	{
		return ismanaged;
	}
	
	void addItem(char cPad, int nPosition, int nNbChars)
	{
		InitializeCachedItem initializeCachedItem = new InitializeCachedItemRepeatingChar(cPad, nPosition, nNbChars);
		arr.add(initializeCachedItem);
	}
	
	void addItem(VarBufferPos buffer, int nOffset, int nNbChars)
	{
		char tChars[] = buffer.getAsCharArray(nOffset, nNbChars);
		int nPos = buffer.nAbsolutePosition+nOffset;
		doAddItem(tChars, nPos);
	}
	
	void addItemForBody(VarBufferPos buffer, int nBodyAbsolutePosition, int nOffset, int nNbChars)
	{
		char tChars[] = buffer.getAsCharArray(nOffset, nNbChars);
		int nPos = nBodyAbsolutePosition+nOffset;
		doAddItem(tChars, nPos);
	}
	
	private void doAddItem(char tChars[], int nPos)
	{		
		InitializeCachedItem initializeCachedItem = new InitializeCachedItemCharsArray(tChars, nPos);
		arr.add(initializeCachedItem);
	}
	
	void applyItems(VarBufferPos varBufferPos, int nCurrentAbsolutePosition)	//, int nOffset)
	{
		int nSize = arr.size();
		for(int n=0; n<nSize; n++)
		{
			InitializeCachedItem initializeCachedItem = arr.get(n);
			initializeCachedItem.apply(nBaseAbsolutePosition, varBufferPos, nCurrentAbsolutePosition);	//, nOffset);			
		}
	}
	
	private boolean isfilled = false;
	private boolean ismanaged = true;
	private ArrayFixDyn<InitializeCachedItem> arr = null;
	private int nBaseAbsolutePosition = 0;
}
