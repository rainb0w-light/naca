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

import java.util.ArrayList;

import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;

import nacaLib.basePrgEnv.BaseProgramManager;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class MoveCorrespondingEntryManager
{
	public MoveCorrespondingEntryManager()
	{	
		arrEntries = new ArrayDyn<MoveCorrespondingEntry>();
		bFilled = false;
	}
	
	void addEntry(MoveCorrespondingEntry entry)
	{
		arrEntries.add(entry);
	}
	
	boolean isFilled()
	{
		return bFilled;
	}
	
	void setFilledAndCompress()
	{
		bFilled = true;
		if(arrEntries != null)
		{		
			if(arrEntries.isDyn())
			{
				int nSize = arrEntries.size();
				MoveCorrespondingEntry arr[] = new MoveCorrespondingEntry[nSize];
				arrEntries.transferInto(arr);
				ArrayFix<MoveCorrespondingEntry> arrVarDefFix = new ArrayFix<MoveCorrespondingEntry>(arr);
				arrEntries = arrVarDefFix;	// replace by a fix one (uning less memory)
			}
		}
	}
	
	void doMoves(BaseProgramManager programManager, int nSourceOffset, int nDestOffset)
	{
		if(arrEntries != null)
		{			
			int nNbEntries = arrEntries.size();
			for(int n=0; n<nNbEntries; n++)
			{
				MoveCorrespondingEntry entry = arrEntries.get(n);
				entry.doMove(programManager, nSourceOffset, nDestOffset);
			}
		}
	}
	
	private boolean bFilled = false; 
	private ArrayFixDyn<MoveCorrespondingEntry> arrEntries = null;
}
