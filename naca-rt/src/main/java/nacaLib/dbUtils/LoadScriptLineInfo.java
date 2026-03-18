/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.dbUtils;

import jlib.misc.StringUtil;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: LoadScriptLineInfo.java,v 1.4 2006/10/24 11:31:38 u930di Exp $
 */
public class LoadScriptLineInfo
{
	private boolean bReplace = false;
	private String csTablePrefix = null;
	private String csUnprefixedTableName = null;
	private String csInddnValue = null;
	
	void setFullTable(String csFullTable)
	{
		csTablePrefix = StringUtil.getTablePrefix(csFullTable);
		csUnprefixedTableName = StringUtil.getUnprefixedTableName(csFullTable);
	}
	
//	String getFullTableName()
//	{
//		return StringUtil.makeFullTableName(csTablePrefix, csUnprefixedTableName);
//	}
	
	String getFullTableName(String csPrefix)
	{
		if(!StringUtil.isEmpty(csTablePrefix) && !csTablePrefix.equalsIgnoreCase("PROD"))
			return StringUtil.makeFullTableName(csTablePrefix, csUnprefixedTableName);
		csTablePrefix = csPrefix;
		return StringUtil.makeFullTableName(csPrefix, csUnprefixedTableName);
	}
	
	String getTablePrefix()
	{
		return csTablePrefix;
	}
	
	String getUnprefixedTableName()
	{
		return csUnprefixedTableName;
	}

	void setReplace(boolean b)
	{
		bReplace = b;
	}
	
	boolean isReplace()
	{
		return bReplace;
	}
	
	void setInddnValue(String csInddnValue)
	{
		csInddnValue = csInddnValue;
	}
		
	String getInddnValue()
	{
		return csInddnValue;
	}
}
