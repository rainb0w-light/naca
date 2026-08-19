/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.xml;

/**
 * @author PJD
 *
 */
public class TagCursor
{
    /** Creates a new tag cursor instance. */
    public TagCursor()
    {
    }

    void setCurrentTag(Tag tag)
    {
        this.tag = tag;
    }

    Tag getCurrentTag()
    {
        return tag;
    }

    void setInvalid()
    {
        tag = null;
    }

    public boolean isValid()
    {
        return tag != null;
    }

    void setNameEnumeration(String csName)
    {
        this.csName = csName;
    }

    String getNameEnumeration()
    {
        return csName;
    }


    Tag tag = null;
    String csName = null;

}
