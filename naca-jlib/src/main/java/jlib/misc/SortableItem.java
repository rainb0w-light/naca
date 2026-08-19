/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/** Provides sortable item behavior. */
public abstract class SortableItem
{
    /** Executes the compare operation. */
    public abstract int compare(SortableItem item);
}
