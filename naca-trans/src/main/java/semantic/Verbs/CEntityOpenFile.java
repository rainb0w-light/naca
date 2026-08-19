/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

/** Provides centity open file behavior. */
public class CEntityOpenFile extends CBaseActionEntity
{
    /** Enumerates supported open mode values. */
    public enum OpenMode
    {
        INPUT,
        OUTPUT,
        INPUT_OUTPUT,
        APPEND
    }
    /** Creates a new centity open file instance. */
    public CEntityOpenFile(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /** Sets the file descriptor. */
    public void setFileDescriptor(CEntityFileDescriptor fd, OpenMode mode)
    {
        eFileDescriptor = fd ;
        eMode = mode;
    }
    protected CEntityFileDescriptor eFileDescriptor = null ;
    protected OpenMode eMode = null ;

    public CEntityFileDescriptor getFileDescriptor()
    {
        return eFileDescriptor;
    }

    public OpenMode getMode()
    {
        return eMode;
    }

    private OpenMode getEffectiveMode()
    {
        return eMode != null || eFileDescriptor == null
            ? eMode
            : eFileDescriptor.getAccessMode();
    }

    public boolean isInputMode()
    {
        return getEffectiveMode() == OpenMode.INPUT;
    }

    public boolean isOutputMode()
    {
        return getEffectiveMode() == OpenMode.OUTPUT;
    }

    public boolean isInputOutputMode()
    {
        return getEffectiveMode() == OpenMode.INPUT_OUTPUT;
    }

    public boolean isAppendMode()
    {
        return getEffectiveMode() == OpenMode.APPEND;
    }

    public boolean isVariableLengthFile()
    {
        return eFileDescriptor != null && eFileDescriptor.isRecordSizeVariable();
    }

}
