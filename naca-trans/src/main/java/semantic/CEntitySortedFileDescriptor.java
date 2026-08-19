/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Collections;
import java.util.List;
import utils.CObjectCatalog;

/** Provides centity sorted file descriptor behavior. */
public class CEntitySortedFileDescriptor extends CEntityFileDescriptor
{

    /** Creates a new centity sorted file descriptor instance. */
    public CEntitySortedFileDescriptor(int line, String name,
                    CObjectCatalog cat)
    {
        super(line, name, cat);
    }

    /** Returns the declaration children. */
    public List<CBaseLanguageEntity> getDeclarationChildren()
    {
        if (lstChildren.isEmpty())
        {
            return Collections.emptyList();
        }
        return Collections.singletonList(lstChildren.getFirst());
    }

}
