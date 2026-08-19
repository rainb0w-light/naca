/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.varEx;

/** Provides cobol constant base behavior. */
public abstract class CobolConstantBase
{
    /** Returns the value. */
    public abstract char getValue();
    /** Returns the stcheck value. */
    public abstract String getSTCheckValue();
}
