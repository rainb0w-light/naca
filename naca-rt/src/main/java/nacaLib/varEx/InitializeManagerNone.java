/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 */
public class InitializeManagerNone extends InitializeManager
{
    /** Creates a new initialize manager none instance. */
    public InitializeManagerNone()
    {
    }

//  public void initialize(VarBufferPos buffer, VarDefBuffer varDefBuffer)
//  {
//      varDefBuffer.initialize(buffer);
//  }


    /** Executes the initialize operation. */
    public void initialize(VarBufferPos buffer, VarDefBuffer varDefBuffer, int nOffset, InitializeCache initializeCache)
    {
        varDefBuffer.initializeAtOffset(buffer, nOffset, initializeCache);
    }
}
