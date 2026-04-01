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
		entries = new ArrayDyn<MoveCorrespondingEntry>();
		isfilled = false;
	}
	
	void addEntry(MoveCorrespondingEntry entry)
	{
		entries.add(entry);
	}
	
	boolean isFilled()
	{
		return isfilled;
	}
	
	void setFilledAndCompress()
	{
		isfilled = true;
		if(entries != null)
		{		
			if(entries.isDyn())
			{
				int nSize = entries.size();
				MoveCorrespondingEntry arr[] = new MoveCorrespondingEntry[nSize];
				entries.transferInto(arr);
				ArrayFix<MoveCorrespondingEntry> varDefFix = new ArrayFix<MoveCorrespondingEntry>(arr);
				entries = varDefFix;	// replace by a fix one (uning less memory)
			}
		}
	}
	
	void doMoves(BaseProgramManager programManager, int nSourceOffset, int nDestOffset)
	{
		if(entries != null)
		{			
			int nNbEntries = entries.size();
			for(int n=0; n<nNbEntries; n++)
			{
				MoveCorrespondingEntry entry = entries.get(n);
				entry.doMove(programManager, nSourceOffset, nDestOffset);
			}
		}
	}
	
	private boolean isfilled = false;
	private ArrayFixDyn<MoveCorrespondingEntry> entries = null;
}
