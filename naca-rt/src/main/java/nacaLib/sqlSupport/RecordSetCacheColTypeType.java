/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.sqlSupport;

import jlib.misc.ArrayDynDiscontinuous;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class RecordSetCacheColTypeType
{
	RecordSetCacheColTypeType()
	{
		arrColsType = new ArrayDynDiscontinuous<RecordColTypeManagerBase>();	
	}
	
	RecordColTypeManagerBase getRecordColTypeManager(int nIndex)
	{
		return arrColsType.get(nIndex);
	}
	
//	void add(RecordColTypeManagerBase recordColTypeManagerBase)
//	{
//		arrColsType.add(recordColTypeManagerBase);
//	}
	
	void set(int nIndex, RecordColTypeManagerBase recordColTypeManagerBase)
	{
		arrColsType.set(nIndex, recordColTypeManagerBase);
	}
	
	void compress()
	{
		// Swap the type inside arrColsType
		if(arrColsType.isDyn())
		{
			int nSize = arrColsType.size();
			RecordColTypeManagerBase arr[] = new RecordColTypeManagerBase[nSize];
			arrColsType.transferInto(arr);
			
			ArrayFix<RecordColTypeManagerBase> arrFix = new ArrayFix<RecordColTypeManagerBase>(arr);
			arrColsType = arrFix;	// replace by a fix one (uning less memory)
		}
	}
	
	private ArrayFixDyn<RecordColTypeManagerBase> arrColsType = null;	// hash table of boolean, indexed by col id, indexed based 0
}
