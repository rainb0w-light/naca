/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.sqlSupport;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import nacaLib.varEx.Var;
import jlib.log.Log;
import jlib.sql.BaseDbColDefinition;
import jlib.sql.DbPreparedCallableStatement;
import jlib.sql.StoredProcParamDescBase;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: StoredProcParamDesc.java,v 1.4 2007/10/17 05:21:53 u930di Exp $
 */
public class StoredProcParamDesc extends StoredProcParamDescBase
{
    private Var varInOut = null;

    void setVar(Var var)
    {
        varInOut = var;
    }

    /** Executes the retrieve out values operation. */
    public void retrieveOutValues(int nParamId, PreparedCallableStatement callableStatement, CSQLStatus sqlStatus)
    {
        nParamId++; // 1 based
        if(sColType == DatabaseMetaData.procedureColumnOut || sColType == DatabaseMetaData.procedureColumnInOut)
        {
            try
            {
                String csOutLang = callableStatement.getOutValueString(nParamId);
                if (varInOut != null) {
                    varInOut.set(csOutLang);
                }
            }
            catch (SQLException e)
            {
                String csState = e.getSQLState();
                String csReason = e.getMessage();
                Log.logImportant("Catched SQLException from stored procedure retrieveOutValues: "+csReason + " State="+csState);
                sqlStatus.setSQLCode("StoredProc", e.getErrorCode(), csReason, csState);

                sqlStatus.setSQLCode(e);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** Executes the fill in value operation. */
    public boolean fillInValue(int nParamId, DbPreparedCallableStatement callableStatement)
    {
        if(varInOut != null)
        {
            BaseDbColDefinition def = colDescriptionInfo.makeDbColDefinition();
            return def.fillCallableStatementParam(nParamId, this, callableStatement);
        }
        return false;
    }

    /** Returns the in value as string. */
    public String getInValueAsString()
    {
        String cs = varInOut.getString();
        return cs;
    }

    /** Returns the in value as double. */
    public double getInValueAsDouble()
    {
        double d = varInOut.getDouble();
        return d;
    }

    /** Returns the in value as int. */
    public int getInValueAsInt()
    {
        int n = varInOut.getInt();
        return n;
    }

    /** Returns the in value as short. */
    public short getInValueAsShort()
    {
        int n = varInOut.getInt();
        return (short)n;
    }

}
