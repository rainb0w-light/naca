/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

public class SortParagHandler
{
	SortParagHandler(SortCommand sortCommand)
	{
		sortCommand = sortCommand;
	}
	
	public void release(Var varRecord)
	{
		sortCommand.release(varRecord);
	}
	
	public RecordDescriptorAtEnd returnSort(SortDescriptor sortDescriptor)
	{
		return sortCommand.returnSort(sortDescriptor);
	}
	
	private SortCommand sortCommand = null;
}
