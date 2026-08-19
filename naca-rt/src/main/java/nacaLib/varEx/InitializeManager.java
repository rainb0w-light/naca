/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author PJD
 *
 */
package nacaLib.varEx;

import nacaLib.base.CJMapObject;

/** Provides initialize manager behavior. */
public abstract class InitializeManager extends CJMapObject
{
    /** Executes the initialize operation. */
    public abstract void initialize(VarBufferPos buffer, VarDefBuffer varDefBuffer, int nOffset, InitializeCache initializeCache);
}
