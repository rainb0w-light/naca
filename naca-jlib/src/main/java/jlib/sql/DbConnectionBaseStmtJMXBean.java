/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;

import java.util.Date;

import jlib.jmxMBean.BaseCloseMBean;
import jlib.misc.DateUtil;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbConnectionBaseStmtJMXBean.java,v 1.2 2008/07/02 12:43:41 u930di Exp $
 */
public class DbConnectionBaseStmtJMXBean extends BaseCloseMBean
{
	private String csStmt = null;
	private long lLastUsageTimeValue = 0;
	
	DbConnectionBaseStmtJMXBean(String csStmt, long lLastUsageTimeValue)
	{
		 csStmt = csStmt;
		 lLastUsageTimeValue = lLastUsageTimeValue;
	}
		
	void cleanup()
	{
	}
	
	protected void buildDynamicMBeanInfo()
	{
		addAttribute("Stmt", getClass(), "Stmt", String.class);
		addAttribute("LastTimeStamp", getClass(), "LastTimeStamp", String.class);
	}
	
	public String getStmt()
	{
		return csStmt;
	}
	
	public String getLastTimeStamp()
	{
		Date date = new Date(lLastUsageTimeValue);
		String cs = DateUtil.getDisplayTimeStamp(date);
		return cs;
	}

}
