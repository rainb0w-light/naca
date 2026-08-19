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

import nacaLib.base.*;
import nacaLib.varEx.VarAndEdit;

public class Pointer extends CJMapObject
{
    public Pointer(VarAndEdit v)
    {
        addressOf = v ;
    }

    public VarAndEdit addressOf = null ;
}
