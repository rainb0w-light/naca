/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

/** Defines the contract for check up and running mbean. */
public interface CheckUpAndRunningMBean
{
    /** Returns whether up. */
    boolean isUp();

    /** Returns the nb check up. */
    int getNbCheckUp();

    /** Returns the inc. */
    public boolean getInc();
    /** Sets the inc. */
    public void setInc(boolean b);
}
