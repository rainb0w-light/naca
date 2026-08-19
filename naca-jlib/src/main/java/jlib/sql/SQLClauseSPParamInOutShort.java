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

import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLClauseSPParamInOutShort.java,v 1.1 2007/10/16 09:47:08 u930di Exp $
 */
public class SQLClauseSPParamInOutShort extends SQLClauseSPParamInOut
{
    private short tsVal[] = null;

    /** Creates a new sqlclause spparam in out short instance. */
    public SQLClauseSPParamInOutShort(SQLClauseSPParamWay wayInOut, short tsVal[])
    {
        super(wayInOut);
        this.tsVal = tsVal;
    }

    protected void setInValueWithException(int nParamId, DbPreparedCallableStatement stmt)
        throws SQLException
    {
        stmt.setInValueWithException(nParamId, tsVal[0]);
    }

    protected void registerOutParameterWithException(int nParamId, DbPreparedCallableStatement stmt)
        throws SQLException
    {
        stmt.registerOutParameterWithException(nParamId, Types.SMALLINT);
    }

    protected void retrieveOutValuesWithException(int nParamId, DbPreparedCallableStatement stmt)
        throws SQLException
    {
        tsVal[0] = stmt.getOutValueShortWithException(nParamId);
    }
}
