/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.File;
import java.io.FilenameFilter;

/** Provides file filter by prefix behavior. */
public class FileFilterByPrefix implements FilenameFilter
{
    private String csPrefix = null;

    /** Creates a new file filter by prefix instance. */
    public FileFilterByPrefix(String csPrefix)
    {
        csPrefix = csPrefix.toUpperCase();
    }

    /** Executes the accept operation. */
    public boolean accept(File dir, String csName)
    {
        if(csPrefix != null && csName != null)
        {
            String cs = csName.toUpperCase();
            if (cs.startsWith(csPrefix)) {
                return true;
            }
        }
        return false;
    }
}
