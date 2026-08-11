/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 */
public class NameManager
{
	static public String getUnprefixedName(String csName)
	{
		int nPosSep = csName.indexOf('.');
		if(nPosSep != -1)
			csName = csName.substring(nPosSep+1);

		nPosSep = csName.indexOf('$');
		if(nPosSep != -1)
			csName = csName.substring(0, nPosSep);		// 1st name that follows the dot (File$X.Y$Z$T -> returns Y)

		return csName;
	}

	static public String getUnprefixedUnindexedName(String csName)
	{
		String cs = getUnprefixedName(csName);
		int n = cs.indexOf("[");
		if(n != -1)
		{
			cs = cs.substring(0, n);
		}
		return cs;
	}
}
