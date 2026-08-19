/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.program;

import nacaLib.base.CJMapObject;

/**
 * @author U930DI
 *
 */
public abstract class CJMapRunnable extends CJMapObject
{
    /** Runs this operation. */
    abstract public void run();
}
