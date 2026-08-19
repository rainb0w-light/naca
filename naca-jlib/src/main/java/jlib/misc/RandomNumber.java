/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package jlib.misc;

import java.util.Random;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: RandomNumber.java,v 1.1 2008/07/02 12:44:35 u930di Exp $
 */
public class RandomNumber
{
    private Random rand = null;
    private static RandomNumber ms_randomNumberSingleton = null;

    /** Creates a new random number instance. */
    public RandomNumber()
    {
        rand = new Random();
    }

    /** Creates a new random number instance. */
    public RandomNumber(int nSeed)
    {
        rand = new Random(nSeed);
    }

    /** Returns the int between. */
    public int getIntBetween(int nMin, int nMax)
    {
        int nRange = nMax - nMin;
        int n = rand.nextInt(nRange);
        n += nMin;

        return n;
    }

    /** Executes the generate int between operation. */
    public static int generateIntBetween(int nMin, int nMax)
    {
        if (ms_randomNumberSingleton == null) {
            ms_randomNumberSingleton = new RandomNumber();
        }
        return ms_randomNumberSingleton.getIntBetween(nMin, nMax);
    }
}
