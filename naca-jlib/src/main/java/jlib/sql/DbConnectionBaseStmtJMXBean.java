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
    private String stmt = null;
    private long lastUsageTimeValue = 0;

    DbConnectionBaseStmtJMXBean(String csStmt, long lastUsageTimeValue)
    {
         this.stmt = csStmt;
         this.lastUsageTimeValue = lastUsageTimeValue;
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
        return stmt;
    }

    /** Returns the last time stamp. */
    public String getLastTimeStamp()
    {
        Date date = new Date(lastUsageTimeValue);
        String cs = DateUtil.getDisplayTimeStamp(date);
        return cs;
    }

}
