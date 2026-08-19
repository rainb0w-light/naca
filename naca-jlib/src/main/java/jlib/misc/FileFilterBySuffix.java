/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.File;
import java.io.FilenameFilter;

/** Provides file filter by suffix behavior. */
public class FileFilterBySuffix implements FilenameFilter
{
    private String csSuffix = null;

    /** Creates a new file filter by suffix instance. */
    public FileFilterBySuffix(String csSuffix)
    {
        csSuffix = csSuffix.toUpperCase();
    }

    /** Executes the accept operation. */
    public boolean accept(File dir, String csName)
    {
        if(csSuffix != null && csName != null)
        {
            String cs = csName.toUpperCase();
            if (cs.endsWith(csSuffix)) {
                return true;
            }
        }
        return false;
    }
}
