package com.publicitas.naca.cloudnative.carddemo.sql;

import java.util.ArrayList;
import java.util.List;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.sql.dsl.CobolSqlTemplate;
import nacaLib.sqlSupport.CSQLStatus;
import nacaLib.sqlSupport.SQLCode;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/** PostgreSQL execution adapter preserving the SQLCODE contract of translated programs. */
public class PostgresCobolSqlTemplate extends CobolSqlTemplate
{
    private final PostgresSqlCodeMapper sqlCodeMapper;

    /** Creates a PostgreSQL adapter bound to one translated program manager. */
    public PostgresCobolSqlTemplate(JdbcTemplate jdbcTemplate, BaseProgramManager programManager,
        PostgresSqlCodeMapper sqlCodeMapper)
    {
        super(jdbcTemplate, programManager);
        this.sqlCodeMapper = sqlCodeMapper;
    }

    @Override
    public CSQLStatus queryForVars(String sql, ParamSetter paramSetter, ResultMapper resultMapper)
    {
        CSQLStatus status = getProgramManager().getSQLStatus();
        try
        {
            List<Object> result = getJdbcTemplate().query(sql,
                statement -> {
                    if (paramSetter != null)
                    {
                        paramSetter.setParameters(statement);
                    }
                },
                rows -> {
                    if (!rows.next())
                    {
                        return null;
                    }
                    List<Object> values = new ArrayList<>();
                    for (int column = 1; column <= rows.getMetaData().getColumnCount(); column++)
                    {
                        values.add(rows.getObject(column));
                    }
                    return values;
                });
            if (result == null)
            {
                status.setSQLCode(SQLCode.SQL_NO_DATA);
                return status;
            }
            resultMapper.mapResults(new ResultContext(result));
            status.setSQLCode(SQLCode.SQL_OK);
        }
        catch (DataAccessException | java.sql.SQLException failure)
        {
            status.setSQLCode(sqlCodeMapper.map(failure));
            getErrorContext().handleError(failure, getProgramManager());
        }
        return status;
    }

    @Override
    public CSQLStatus executeUpdate(String sql, ParamSetter paramSetter)
    {
        CSQLStatus status = getProgramManager().getSQLStatus();
        try
        {
            int rows = getJdbcTemplate().update(sql, statement -> {
                if (paramSetter != null)
                {
                    paramSetter.setParameters(statement);
                }
            });
            status.setRowsAffected(rows);
            status.setSQLCode(SQLCode.SQL_OK);
        }
        catch (DataAccessException failure)
        {
            status.setSQLCode(sqlCodeMapper.map(failure));
            getErrorContext().handleError(failure, getProgramManager());
        }
        return status;
    }
}
