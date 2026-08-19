/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/** Defines the contract for log center mbean. */
public interface LogCenterMBean
{
    /** Returns the enable. */
    public boolean getEnable();
    /** Sets the enable. */
    public void setEnable(boolean b);

    /** Returns the level. */
    public String getLevel();
    /** Sets the level. */
    public void setLevel(String csLevel);
}
