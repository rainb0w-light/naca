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

public abstract class CBaseMapFieldLoader
{
    public abstract String getFieldValue(String fieldName);
    public abstract String getIDPage() ;
    public abstract boolean isFieldModified(String fieldName);
}
