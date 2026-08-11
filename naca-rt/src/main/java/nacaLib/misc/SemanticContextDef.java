/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.misc;

import java.util.HashMap;

/**
 * @author u930di
 *
 */
public class SemanticContextDef
{
	public SemanticContextDef()
	{
	}

	public String getSemanticContextValueDefinition(String csTable, String csCol)
	{
		String csTableColName = SemanticContextDef.getTableColName(csTable, csCol);
		return getSemanticContextValueDefinition(csTableColName);
	}

	public String getSemanticContextValueDefinition(String csTableColName)
	{
		String csSemanticContext = hashDBSemanticContext.get(csTableColName);
		return csSemanticContext;
	}

	public void setSemanticContextValueDefinition(String csTable, String csCol, String csSemanticContext)
	{
		String csTableColName = SemanticContextDef.getTableColName(csTable, csCol);
		hashDBSemanticContext.put(csTableColName, csSemanticContext);
	}


	static public String getTableColName(String csTable, String csCol)
	{
		return csTable + "/" + csCol;
	}

	private HashMap<String, String> hashDBSemanticContext = new HashMap<String, String>();
}
