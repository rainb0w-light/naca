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

public abstract class CCallParam extends CJMapObject
{
    public CCallParam()
    {
    }

    public abstract int getParamLength();
    public abstract void MapOn(Var varLinkageSection);
}
