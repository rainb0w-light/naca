/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.semanticContext;

/**
 * @author U930DI
 *
 */
public class CSemanticItem
{
	CSemanticItem(String csScreenId, CMenuDef MenuDef)
	{
		this.csScreenId = csScreenId;
		this.menuDef = MenuDef;
	}

	String csScreenId = null;
	CMenuDef menuDef = null;
}
