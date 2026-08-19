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
import java.sql.SQLException;
import jlib.misc.AsciiEbcdicConverter;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbColDefinitionChar.java,v 1.8 2007/06/06 18:22:14 u930bm Exp $
 */
public class DbColDefinitionChar extends BaseDbColDefinition
{
    private int nLength = 0;

    DbColDefinitionChar(ColDescriptionInfo colDescription)
    {
        super(colDescription);
        nLength = colDescription.getPrecision();
    }

    /** Returns the byte value. */
    public byte[] getByteValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput)
    {
        try
        {
            String value = resultSet.getString(nCol1Based);
            byte[] aBytes = value.getBytes();
            if (bEbcdicOutput) {   // Must outout in ebcdic
                AsciiEbcdicConverter.swapByteAsciiToEbcdic(aBytes, 0, aBytes.length);
            }
            return aBytes;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

//  public int setByteValue(byte arrByteValue[], int nSourceOffset, boolean bEbcdicInput, ColValueGeneric colValueGenericDest)
//  {
//      if(bEbcdicInput)    // Must outout in ebcdic
//          AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, nSourceOffset, nLength);
//      String cs = new String(arrByteValue, nSourceOffset, nLength);
//      colValueGenericDest.setValue(cs);
//
//      return nLength;
//  }

    /** Sets the byte value in stmt col. */
    public int setByteValueInStmtCol(
        DbColDefErrorManager dbColDefErrorManager,
        DbPreparedStatement stmt,
        int nCol,
        byte arrByteValue[],
        int nSourceOffset,
        boolean bEbcdicInput)
    {
        if (bEbcdicInput) {    // Must outout in ebcdic
            AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, nSourceOffset, nLength);
        }
        String value = new String(arrByteValue, nSourceOffset, nLength);
        stmt.setColParam(nCol, value);

        return nLength;
    }

    /** Executes the fill callable statement param operation. */
    public boolean fillCallableStatementParam(
        int nParamId,
        StoredProcParamDescBase storedProcParamDescBase,
        DbPreparedCallableStatement callableStatement)
    {
        String cs = storedProcParamDescBase.getInValueAsString();
        return callableStatement.setInValue(nParamId, cs);
    }

    /** Returns the excel value. */
    public byte[] getExcelValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput)
    {
        try
        {
            String value = resultSet.getString(nCol1Based);
            value = value.trim().replace("\"", "'");
            if (value.length() == 0) {
                value = " ";
            }
            value = "\"" + value + "\"";
            byte[] aBytes = value.getBytes();
            if (bEbcdicOutput) {   // Must outout in ebcdic
                AsciiEbcdicConverter.swapByteAsciiToEbcdic(aBytes, 0, aBytes.length);
            }
            return aBytes;
        }
        catch (SQLException e)
        {
            return null;
        }
    }
}
