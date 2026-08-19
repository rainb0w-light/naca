/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

import java.sql.SQLException;

import jlib.misc.StringUtil;

import nacaLib.base.CJMapObject;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/**
 * @author U930CV
 *
 */
public class CSQLStatus extends CJMapObject
{
    private int nSQLCode = 0 ;
    private int nLastNbRecordUpdatedInsertedDeleted = 0;    // Accessible by getSQLDiagnosticCode(3)
    private String csQueryString = null;
    //private String csSourceFileLine = null;
    private String csMethod = null;
    private String csReason = null;
    private String csReasonParams = null;
    private String csReasonValues = null;

    /** Creates a new csqlstatus instance. */
    public CSQLStatus()
    {
    }

    public int getSQLCode()
    {
        return nSQLCode ;
    }

    /** Executes the reset operation. */
    public void reset()
    {
        nSQLCode = 0 ;
        csMethod = null;
        csReason = null;
        csReasonParams = null;
        csReasonValues = null;
        //csQueryString = null;
        //csSourceFileLine = null;
    }

    /** Sets the sqlcode. */
    public void setSQLCode(int n)
    {
        reset();
        nSQLCode = n ;
    }

    /** Sets the sqlcode ok. */
    public void setSQLCodeOk()
    {
        reset();
        nSQLCode = SQLCode.SQL_OK;
    }

    /** Sets the sqlcode. */
    public void setSQLCode(SQLException e)
    {
        reset();
        nSQLCode = e.getErrorCode();
        csReason = "SQL Exception (" + nSQLCode + "):" + e.getMessage()  + " SQLState="+ e.getSQLState();
    }

    /** Sets the sqlcode. */
    public void setSQLCode(String csMethod, SQLException e, String csQueryString/*, String csSourceFileLine*/, SQL sql)
    {
        nSQLCode = e.getErrorCode();
        this.csMethod = csMethod;
        csReason = "SQL Exception (" + nSQLCode + "):" + e.getMessage()  + " SQLState="+ e.getSQLState();
        if(sql != null)
        {
            csReasonParams = sql.getDebugParams();
            csReasonValues = sql.getDebugValues();
        }
        this.csQueryString = csQueryString;
    }

    /** Executes the fill last sqlcode error text operation. */
    public void fillLastSQLCodeErrorText()
    {
        TempCache cache = TempCacheLocator.getTLSTempCache();
        cache.fillLastSQLCodeErrorText(this);
    }

    /** Sets the sqlcode. */
    public void setSQLCode(String csMethod, int nCode, String csReason, String csQueryString)   //, String csSourceFileLine)
    {
        csReasonParams = null;
        csReasonValues = null;

        nSQLCode = nCode;
        this.csMethod = csMethod;
        this.csReason = csReason;
        this.csQueryString = csQueryString;
    }

    public void setQuery(String csQueryString)
    {
        this.csQueryString = csQueryString;
    }

    public boolean isLastSQLCodeAnError()
    {
        return SQLCode.isError(nSQLCode);
    }

    public boolean isLastSQLCodeConnectionKiller()
    {
        return SQLCode.isConnectionKillerSQLCode(nSQLCode);
    }

    /** Returns the sqldiagnostic code. */
    public int getSQLDiagnosticCode(int n)
    {
        // See http://publib.boulder.ibm.com/infocenter/dzichelp/index.jsp?topic=/com.ibm.db2.doc.apsg/bjnqmstr370.htm
        if (n == 3) {
            return nLastNbRecordUpdatedInsertedDeleted;
        }
        return 0;
    }

    void setLastNbRecordUpdatedInsertedDeleted(int n)
    {
        nLastNbRecordUpdatedInsertedDeleted = n;
    }

    /**
     * Set the number of rows affected by the last SQL operation.
     * This is a public method for use by the new SQL DSL.
     * @param n the number of rows affected
     */
    public void setRowsAffected(int n)
    {
        nLastNbRecordUpdatedInsertedDeleted = n;
    }

    /** Executes the on error goto operation. */
    public CSQLStatus onErrorGoto(Paragraph para)
    {
        SQLErrorManager sqlErrorManager = new SQLErrorManager();
        sqlErrorManager.manageOnErrorGoto(para, this);
        return this;
    }

    /** Executes the on error goto operation. */
    public CSQLStatus onErrorGoto(Section section)
    {
        SQLErrorManager sqlErrorManager = new SQLErrorManager();
        sqlErrorManager.manageOnErrorGoto(section, this);
        return this;
    }

    /** Executes the on error continue operation. */
    public CSQLStatus onErrorContinue()
    {
        SQLErrorManager sqlErrorManager = new SQLErrorManager();
        sqlErrorManager.manageOnErrorContinue(this);
        return this;
    }
    /** Executes the on warning goto operation. */
    public CSQLStatus onWarningGoto(Paragraph paragraphSQGErrorGoto)
    {
        // TODO
        return this;
    }

    /** Executes the on warning goto operation. */
    public CSQLStatus onWarningGoto(Section section)
    {
        // TODO
        return this;
    }

    /** Executes the on warning continue operation. */
    public CSQLStatus onWarningContinue()
    {
        // TODO
        return this;
    }

    public String getReason()
    {
        return csReason;
    }

    public String getReasonParams()
    {
        return csReasonParams;
    }

    public String getReasonValues()
    {
        return csReasonValues;
    }

    public String getQueryString()
    {
        return csQueryString;
    }

    public String getMethod()
    {
        return csMethod;
    }

//  public String getSourceFileLine()
//  {
//      return csSourceFileLine;
//  }
//
    /** Returns a string representation of this value. */
    public String toString()
    {
        StringBuffer sb = getAsStringBuffer();
        return sb.toString();
    }

    /** Returns the as string buffer. */
    public StringBuffer getAsStringBuffer()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getReason());
        sb.append(" | SQLCode:"+nSQLCode);
        sb.append(" | Query:");
        sb.append(csQueryString);
        if (!StringUtil.isEmpty(csReasonParams))
        {
            sb.append(" | Params:");
            sb.append(csReasonParams);
        }
        if (StringUtil.isEmpty(csReasonValues))
        {
            sb.append(" | Values:");
            sb.append(csReasonValues);
        }
        return sb;
    }

}
