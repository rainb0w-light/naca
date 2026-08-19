/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.varEx;

import nacaLib.base.CJMapObject;

/** Provides ccall param behavior. */
public abstract class CCallParam extends CJMapObject
{
    /** Creates a new ccall param instance. */
    public CCallParam()
    {
    }

    /** Returns the param length. */
    public abstract int getParamLength();
    /** Executes the map on operation. */
    public abstract void MapOn(Var varLinkageSection);
}
