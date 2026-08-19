/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

package jlib.sql;

import java.sql.Connection;

import jlib.exception.TechnicalException;

/** Provides db connection behavior. */
public class DbConnection extends DbConnectionBase
{
    /** Creates a new db connection instance. */
    public DbConnection(
        Connection conn,
        String csPrefId,
        String csEnv,
        boolean bUseCachedStatements,
        boolean bUseJmx,
        DbDriverId dbDriverId)
    {
        super(conn, csPrefId, csEnv, bUseCachedStatements, bUseJmx, dbDriverId);
    }

    /** Creates a new db connection instance. */
    public DbConnection(Connection conn, String csEnv, boolean bUseCachedStatements)
    {
        super(conn, "", csEnv, bUseCachedStatements, false, null);
    }

    /** Creates the and prepare. */
    public DbPreparedStatement createAndPrepare(String csQuery, boolean bHoldability)
    {
        DbPreparedStatement preparedStatement = new DbPreparedStatement();
        if (preparedStatement.prepare(this, csQuery, bHoldability)) {
            return preparedStatement;
        }
        return null;
    }

    /** Creates the and prepare with exception. */
    public DbPreparedStatement createAndPrepareWithException(String csQuery, boolean bHoldability)
        throws TechnicalException
    {
        DbPreparedStatement preparedStatement = new DbPreparedStatement();
        if (preparedStatement.prepareWithException(this, csQuery, bHoldability)) {
            return preparedStatement;
        }
        return null;
    }

    /** Executes the prepare callable statement operation. */
    public boolean prepareCallableStatement(
        DbPreparedCallableStatement preparedCallableStatement,
        String csStoredProcName,
        int nNbParamToProvide)
    {
        return false;   // unsupported in jlib
    }
}
