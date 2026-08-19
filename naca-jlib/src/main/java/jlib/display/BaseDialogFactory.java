/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.display;

import jlib.xml.Tag;

/**
 * @author U930CV
 *
 */
public abstract class BaseDialogFactory
{

    /**
     * @param context
     * @return
     */
    public abstract BaseDialog getInitialDialog(DisplayContext context) ;

    /**
     * @param config
     * @param tagFactory
     */
    public abstract void Init(DisplayConfig config, Tag tagFactory) ;

}
