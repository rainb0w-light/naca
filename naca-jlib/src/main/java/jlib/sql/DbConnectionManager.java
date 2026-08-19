/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

package jlib.sql;

import java.sql.Connection;


/**
 * @author PJD
 *
 */
public class DbConnectionManager extends DbConnectionManagerBase
{
    /** Creates a new db connection manager instance. */
    public DbConnectionManager()
    {
        super();
    }

    /** Creates the connection. */
    public DbConnectionBase createConnection(
        Connection connection,
        String csPrefId,
        String csEnvironment,
        boolean bUseStatementCache,
        boolean bUseJmx,
        DbDriverId dbDriver)
    {
        DbConnection sqlConnection = new DbConnection(
            connection,
            csPrefId,
            dbConnectionParam.getEnvironment(),
            bUseStatementCache,
            bUseJmx,
            dbDriver);
        return sqlConnection;
    }
}
