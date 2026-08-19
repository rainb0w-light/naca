/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/**
 * @author PJD
 *
 */
public abstract class LogPatternLayout
{
    abstract String getMessage(LogParams logParams);
    abstract String format(LogParams logParams, int n);
    abstract int getNbLoop(LogParams logParams);
}
