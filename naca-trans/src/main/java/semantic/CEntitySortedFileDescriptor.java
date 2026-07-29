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

public class CEntitySortedFileDescriptor extends CEntityFileDescriptor
{

	public CEntitySortedFileDescriptor(int line, String name,
					CObjectCatalog cat)
	{
		super(line, name, cat);
	}

	public List<CBaseLanguageEntity> getDeclarationChildren()
	{
		if (lstChildren.isEmpty())
		{
			return Collections.emptyList();
		}
		return Collections.singletonList(lstChildren.getFirst());
	}

}
