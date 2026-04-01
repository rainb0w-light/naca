/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

public class RecordDescriptorAtEnd
{
	public static RecordDescriptorAtEnd End = new RecordDescriptorAtEnd(true);
	public static RecordDescriptorAtEnd NotEnd = new RecordDescriptorAtEnd(false);
	
	private RecordDescriptorAtEnd(boolean isend)
	{
		this.isend = isend;
	}
	
	public boolean atEnd()
	{
		return isend;
	}
	
	private boolean isend = false;
}
