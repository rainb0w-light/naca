/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import nacaLib.program.Paragraph;
import nacaLib.program.Section;

/** Signals a cgoto exception condition. */
public class CGotoException extends NacaRTException
{
    private static final long serialVersionUID = 1L;
    public Paragraph paragraph = null;
    public Section section = null;

    /** Creates a new cgoto exception instance. */
    public CGotoException(Paragraph functor)
    {
        super();
        paragraph = functor;
    }
    /** Creates a new cgoto exception instance. */
    public CGotoException(Section functor)
    {
        super();
        section = functor;
    }
}
