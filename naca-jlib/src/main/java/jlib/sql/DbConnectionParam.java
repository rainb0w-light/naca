/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.Driver;
import java.util.Properties;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbConnectionParam.java,v 1.7 2008/07/02 12:44:21 u930di Exp $
 */
public class DbConnectionParam
{
	Driver driver = null;
	String csUrl = "" ;
	String csEnvironment = "" ;
	String csPackage = "" ;
	Properties propertiesUserPassword = null;
	String csConnectionUrlOptionalParams = null;
	boolean isautoCommit = false;
	boolean iscloseCursorOnCommit = false;
	
	public DbDriverId getDbDriverId()
	{
		return DbDriverId.getByClassName(driver.getClass().toString());
	}
	
	String getEnvironment()
	{
		return csEnvironment;
	}
	
	void setEnvironment(String cs)
	{
		csEnvironment = cs;
	}
}
