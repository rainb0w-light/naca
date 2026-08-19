/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

/**
 * @author U930CV
 *
 */

package nacaLib.basePrgEnv;

/** Provides cbase map field loader behavior. */
public abstract class CBaseMapFieldLoader
{
    /** Returns the field value. */
    public abstract String getFieldValue(String fieldName);
    /** Returns the idpage. */
    public abstract String getIDPage() ;
    /** Returns whether field modified. */
    public abstract boolean isFieldModified(String fieldName);
}
