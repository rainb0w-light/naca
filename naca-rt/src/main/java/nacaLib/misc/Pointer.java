/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */


/**
 * @author U930CV
 *
 */
package nacaLib.misc;

import nacaLib.base.CJMapObject;
import nacaLib.varEx.VarAndEdit;

/** Provides pointer behavior. */
public class Pointer extends CJMapObject
{
    /** Creates a new pointer instance. */
    public Pointer(VarAndEdit v)
    {
        addressOf = v ;
    }

    public VarAndEdit addressOf = null ;
}
