/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import nacaLib.program.*;

public class CGotoException extends NacaRTException
{
	private static final long serialVersionUID = 1L;
	public Paragraph paragraph = null;
	public Section section = null;
	
	public CGotoException(Paragraph functor)
	{
		super();
		paragraph = functor;
	}
	public CGotoException(Section functor)
	{
		super();
		section = functor;
	}
}
