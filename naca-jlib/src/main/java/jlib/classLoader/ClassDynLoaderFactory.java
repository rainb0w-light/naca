/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.classLoader;

/** Provides class dyn loader factory behavior. */
public class ClassDynLoaderFactory
{
    protected static ClassDynLoaderFactory ms_instance =  null;

    protected ClassDynLoaderFactory()
    {
    }

    /** Returns the instance. */
    public static ClassDynLoaderFactory getInstance()
    {
        if (ms_instance == null) {
            ms_instance = new ClassDynLoaderFactory();
        }
        return ms_instance;
    }

    /** Executes the make operation. */
    public ClassDynLoader make()
    {
        return new ClassDynLoader();
    }

    /** Executes the preload jar operation. */
    public void preloadJar(String csJarFile)
    {
        int n = 0;
    }
}
