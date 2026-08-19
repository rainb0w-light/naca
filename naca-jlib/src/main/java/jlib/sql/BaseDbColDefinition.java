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

import java.sql.ResultSet;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BaseDbColDefinition.java,v 1.5 2007/03/16 08:41:35 u930di Exp $
 */
public abstract class BaseDbColDefinition
{
    private String name = null;

    /** Provides col insert value behavior. */
    public class ColInsertValue
    {
        public int nOffset = 0;
        public String csValue = null;
    }

    /** Creates a new base db col definition instance. */
    public BaseDbColDefinition(ColDescriptionInfo colDescription)
    {
        name = colDescription.getColName();
    }

    public String getColumnName()
    {
        return name;
    }

    /** Executes the fill callable statement param operation. */
    public abstract boolean fillCallableStatementParam(
        int nParamId,
        StoredProcParamDescBase storedProcParamDescBase,
        DbPreparedCallableStatement callableStatement);
    /** Returns the byte value. */
    public abstract byte[] getByteValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput);
    // public abstract int setByteValue(byte arrByteValue[], int nSourceOffset, boolean bEbcdicInput, ColValueGeneric colValueGenericDest);
    /** Sets the byte value in stmt col. */
    public abstract int setByteValueInStmtCol(
        DbColDefErrorManager dbColDefErrorManager,
        DbPreparedStatement stmt,
        int nCol,
        byte arrByteValue[],
        int nSourceOffset,
        boolean bEbcdicInput);
    /** Returns the excel value. */
    public abstract byte[] getExcelValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput);
}
