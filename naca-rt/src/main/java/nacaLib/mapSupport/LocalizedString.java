/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.mapSupport;

import jlib.misc.MapStringByString;

/**
 * @author sly
 *
 */
public class LocalizedString
{
	public LocalizedString()
	{
	}
	/*
	LocalizedString(String cs)
	{
		csName = cs ;
	}
		*/
	public String getTextForLanguage(String csLangId)
	{
		String cs = tabTexts.get(csLangId.trim());
		return cs;
	}

	public LocalizedString text(String csId, String text)
	{
		tabTexts.put(csId, text);
		return this ;
	}

	//protected String csName = "" ;
	protected MapStringByString tabTexts = new MapStringByString() ;
}
