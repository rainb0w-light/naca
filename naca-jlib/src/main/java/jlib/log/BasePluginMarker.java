/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package jlib.log;

/** Provides base plugin marker behavior. */
public abstract class BasePluginMarker
{
    /** Executes the info operation. */
    public abstract void info(String csText);
    /** Executes the warn operation. */
    public abstract void warn(String csText);
    /** Executes the error operation. */
    public abstract void error(String csText);
}
