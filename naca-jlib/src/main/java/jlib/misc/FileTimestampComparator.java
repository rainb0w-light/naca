/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.File;
import java.util.Comparator;


/**
 * @author PJD
 *
 */
public class FileTimestampComparator implements Comparator<File>
{
    FileTimestampComparator()
    {
    }

    public int compare(File file1, File file2)
    {
        long l1 = file1.lastModified();
        long l2 = file2.lastModified();
        if(l1 - l2 < 0)
            return -1;
        if(l1 == l2)
            return 0;
        return 1;
    }
}
