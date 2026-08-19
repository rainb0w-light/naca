/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import java.util.ArrayList;

import jlib.xml.Tag;

/**
 * @author PJD
 *
 */
public class CallStackExclusion
{
    CallStackExclusion()
    {
    }

    void fillExcluded(Tag tagSettings)
    {
        exclude = new ArrayList<String>();
        Tag tagCallLocation = tagSettings.getEnumChild("CallLocation");
        while(tagCallLocation != null)
        {
            String csExcludeName = tagCallLocation.getVal("Exclude");
            exclude.add(csExcludeName);
            tagCallLocation = tagSettings.getEnumChild("CallLocation");
        }
    }

    boolean doNotContains(String csClassName)
    {
        if(exclude != null)
        {
            int nNbExclusion = exclude.size();
            for(int n=0; n<nNbExclusion; n++)
            {
                String csExclude = exclude.get(n);
                if (csClassName.startsWith(csExclude)) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<String> exclude = null;
}
