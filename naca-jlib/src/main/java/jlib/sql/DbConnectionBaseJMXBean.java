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

import java.util.ArrayList;

import jlib.jmxMBean.BaseCloseMBean;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbConnectionBaseJMXBean.java,v 1.1 2008/06/20 08:11:13 u930di Exp $
 */

public class DbConnectionBaseJMXBean extends BaseCloseMBean
{
	private DbConnectionBase dbConnectionBase = null;
	private boolean isshowStatements = false;
	private DbConnectionBaseStmtJMXBean dbConnectionBaseStmtJMXBean = null;
	private ArrayList<DbConnectionBaseStmtJMXBean> stmts = null;
	
	DbConnectionBaseJMXBean(DbConnectionBase dbConnectionBase)
	{
		 dbConnectionBase = dbConnectionBase;
	}
	
	void cleanup()
	{
		isshowStatements = false;	// Hide sttm beans
		doSetShowStatments();
		dbConnectionBaseStmtJMXBean = null;		
		dbConnectionBase = null;
	}
	
	protected void buildDynamicMBeanInfo()
	{
		addAttribute("AreStatementsShown", getClass(), "AreStatementsShown", boolean.class);
		addAttribute("NbCachedStatements", getClass(), "NbCachedStatements", int.class);		
		addOperation("ShowStatments", getClass(), "setShowStatments");
	}
	
	public int getNbCachedStatements()
	{
		if(dbConnectionBase != null)
			return dbConnectionBase.getNbCachedStatements();
		return 0;
	}
	
	public boolean getAreStatementsShown()
	{
		return isshowStatements;
	}
	
	public void setShowStatments()
	{
		isshowStatements = !isshowStatements;
		doSetShowStatments();
	}
	
	synchronized void doSetShowStatments()
	{
		if(isshowStatements)	//&& !isBeanCreated())
		{
			dbConnectionBase.createStmtJMXBeans(this, getMBeanName() + "_Stmt", getMBeanName());
		}
		else if(!isshowStatements)	// && isBeanCreated())
		{
			if(stmts != null)
			{
				for(int n = 0; n< stmts.size(); n++)
				{
					DbConnectionBaseStmtJMXBean bean = stmts.get(n);
					bean.unregisterMBean();
				}
			}
		}
	}	
	
	synchronized void add(DbConnectionBaseStmtJMXBean dbConnectionBaseStmtJMXBean)
	{
		if(stmts == null)
			stmts = new ArrayList<DbConnectionBaseStmtJMXBean>();
		stmts.add(dbConnectionBaseStmtJMXBean);
	}
}
