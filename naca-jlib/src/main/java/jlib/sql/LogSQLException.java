/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.SQLException;
import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogExceptionEvent;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;


/** Signals a log sqlexception condition. */
public class LogSQLException extends LogExceptionEvent
{
    /** Creates a new log sqlexception instance. */
    public LogSQLException()
    {
        super(LogEventType.Error, LogFlowStd.Any, LogLevel.Normal);
    }

    /** Executes the log operation. */
    public static LogEvent log(SQLException e)
    {
        LogSQLException event = new LogSQLException();
        event.fillExceptionMembers(e);
        event.fillMember("Code", e.getErrorCode());
        event.fillMember("SQLState", e.getSQLState());
        event.fillMember("Message", e.getMessage());
        Log.log(null, event, "SQL Exception");
        return event;
    }

    /** Executes the log operation. */
    public static LogEvent log(SQLException e, String csStatement)
    {
        LogSQLException event = new LogSQLException();
        event.fillExceptionMembers(e);
        event.fillMember("Code", e.getErrorCode());
        event.fillMember("SQLState", e.getSQLState());
        event.fillMember("Message", e.getMessage());
        event.fillMember("Statement", csStatement);
        Log.log(null, event, "SQL Exception");
        return event;
    }
}
