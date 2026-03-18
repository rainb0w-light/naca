/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.stringSupport;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class ConcatTo
{
	private Concat concat = null;
	
	ConcatTo(Concat concat)
	{
		concat = concat;
	}
	
	public boolean failed()
	{
		return concat.failed();
	}
}
