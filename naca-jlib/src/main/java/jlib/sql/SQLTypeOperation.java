/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package jlib.sql;

import jlib.misc.StringUtil;

//import jlib.misc.StringRef;
//import jlib.misc.StringUtil;

// import nacaLib.base.*;

public class SQLTypeOperation
{
    public static final SQLTypeOperation Select = new SQLTypeOperation(true);
    public static final SQLTypeOperation CursorSelect = new SQLTypeOperation(true);
    public static final SQLTypeOperation Insert = new SQLTypeOperation(true);
    public static final SQLTypeOperation Update = new SQLTypeOperation(true);
    public static final SQLTypeOperation Delete = new SQLTypeOperation(true);
    public static final SQLTypeOperation Cursor = new SQLTypeOperation(true);
    public static final SQLTypeOperation Create = new SQLTypeOperation(true);
    public static final SQLTypeOperation Drop = new SQLTypeOperation(true);
    public static final SQLTypeOperation Commit = new SQLTypeOperation(false);
    public static final SQLTypeOperation Rollback = new SQLTypeOperation(false);
    public static final SQLTypeOperation Lock = new SQLTypeOperation(true);
    public static final SQLTypeOperation Declare = new SQLTypeOperation(false);

    private boolean isexecuteWithStatement;

    private SQLTypeOperation(boolean bExecuteWithStatement)
    {
        this.isexecuteWithStatement = bExecuteWithStatement;
    }

    public boolean executeWithStatement()
    {
        return isexecuteWithStatement;
    }

    public static SQLTypeOperation determineOperationType(String csQuery, boolean bCursor)
    {
        String sqlOperation = StringUtil.getFirstWordWithStopList(csQuery, ";");
        return getSQLTypeOperation(sqlOperation, bCursor);
    }

    private static SQLTypeOperation getSQLTypeOperation(String s, boolean bCursor)
    {
        if(s.equalsIgnoreCase("SELECT"))
        {
            if(bCursor)
                return SQLTypeOperation.CursorSelect;
            else
                return SQLTypeOperation.Select;
        }
        else if(s.equalsIgnoreCase("DELETE"))
            return SQLTypeOperation.Delete;
        else if(s.equalsIgnoreCase("UPDATE"))
            return SQLTypeOperation.Update;
        else if(s.equalsIgnoreCase("INSERT"))
            return SQLTypeOperation.Insert;
        else if(s.equalsIgnoreCase("COMMIT"))
            return SQLTypeOperation.Commit;
        else if(s.equalsIgnoreCase("ROLLBACK"))
            return SQLTypeOperation.Rollback;
        else if(s.equalsIgnoreCase("CREATE"))
            return SQLTypeOperation.Create;
        else if(s.equalsIgnoreCase("DROP"))
            return SQLTypeOperation.Drop;
        else if(s.equalsIgnoreCase("LOCK"))
            return SQLTypeOperation.Lock;
        else if(s.equalsIgnoreCase("DECLARE"))
            return SQLTypeOperation.Declare;
        else if(s.equalsIgnoreCase("_SELECT"))
        {
            if(bCursor)
                return SQLTypeOperation.CursorSelect;
            else
                return SQLTypeOperation.Select;
        }

        return null;
    }

    public static int minPositive(int nEnd1, int nEnd2)
    {
        if(nEnd1 >= 0 && nEnd2 >= 0)
        {
            if(nEnd1 < nEnd2)
                return nEnd1;
            return nEnd2;
        }

        if(nEnd1 >= 0)
            return nEnd1;

        if(nEnd2 >= 0)
            return nEnd2;
        return -1;
    }

    static private String addLeadingTablePrefix(String begining, String env, String forcedReplacedPrefix, String querry)
    {
        int nPos = -1 ;
        int i = 0;
        do
        {
            nPos = querry.indexOf(',', i) ;
            while (querry.charAt(i) == ' ')
            {
                begining += ' ' ;
                i++ ;
            }
            if (nPos == -1)
            {
                String right = querry.substring(i) ;
                begining += setPrefixIfRequired(env, right, forcedReplacedPrefix);
            }
            else
            {
                String right = querry.substring(i, nPos) ;
                begining += setPrefixIfRequired(env, right, forcedReplacedPrefix);
                begining += ',';
                i = nPos+1 ;
            }
        }
        while (nPos != -1) ;

        return begining;
    }

    private static int getPositionFirstStopListKeywordNextFrom(String queryUpper, int nStart)
    {
        int nEndWhere = queryUpper.indexOf("WHERE", nStart) ;

        // search for position of keyword following the where
        int nEndParenthesis = queryUpper.indexOf(")", nStart) ;
        int nEnd = minPositive(nEndWhere, nEndParenthesis);

        int nEndOrder = queryUpper.indexOf("ORDER", nStart) ;
        nEnd = minPositive(nEnd, nEndOrder);

        int nEndGroup = queryUpper.indexOf("GROUP BY", nStart) ;
        nEnd = minPositive(nEnd, nEndGroup);

        int nEndForUpdate = queryUpper.indexOf("FOR UPDATE", nStart) ;
        nEnd = minPositive(nEnd, nEndForUpdate);

        int nEndForUnion = queryUpper.indexOf("UNION", nStart) ;
        nEnd = minPositive(nEnd, nEndForUnion);

        int nEndJoin = queryUpper.indexOf("JOIN", nStart) ;
        nEnd = minPositive(nEnd, nEndJoin);
        return nEnd;
    }


    private static String addEnvironmentPrefixStandardParser(
        String env,
        String query,
        String queryUpper,
        SQLTypeOperation typeOperation,
        String forcedReplacedPrefix,
        boolean bSupportJoin)
    {
        int nStart = 0 ;

        int n = queryUpper.indexOf("SELECT");
        if (n>=0)
        {
            nStart = n +7 ;
            n = queryUpper.indexOf("FROM", nStart);
            while (n>=0)
            {
                nStart = n +5 ;
                int i = 0 ;
                String begining = query.substring(0, nStart) ;
                String querry = query.substring(nStart) ;

                int nEndWhere = queryUpper.indexOf("WHERE", nStart) ;

                int nEndParenthesis = queryUpper.indexOf(")", nStart) ;
                int nEnd = minPositive(nEndWhere, nEndParenthesis);

                int nEndOrder = queryUpper.indexOf("ORDER", nStart) ;
                nEnd = minPositive(nEnd, nEndOrder);

                int nEndGroup = queryUpper.indexOf("GROUP BY", nStart) ;
                nEnd = minPositive(nEnd, nEndGroup);

                int nEndForUpdate = queryUpper.indexOf("FOR UPDATE", nStart) ;
                nEnd = minPositive(nEnd, nEndForUpdate);

                int nEndForUnion = queryUpper.indexOf("UNION", nStart) ;
                nEnd = minPositive(nEnd, nEndForUnion);

                if(bSupportJoin)
                {
                    int nEndJoin = queryUpper.indexOf("JOIN", nStart) ;
                    nEnd = minPositive(nEnd, nEndJoin);
                }

                String end = "" ;
                if (nEnd != -1)
                {
                    end = query.substring(nEnd) ;
                    querry = query.substring(nStart, nEnd) ;
                }
                int nPos = -1 ;
                do
                {
                    nPos = querry.indexOf(',', i) ;
                    while (querry.charAt(i) == ' ')
                    {
                        begining += ' ' ;
                        i++ ;
                    }
                    if (nPos == -1)
                    {
                        String right = querry.substring(i) ;
                        right = setPrefixIfRequired(env, right, forcedReplacedPrefix);
                        begining += right;
                    }
                    else
                    {
                        String right = querry.substring(i, nPos) ;
                        right = setPrefixIfRequired(env, right, forcedReplacedPrefix);
                        begining += right;
                        begining += ',';
                        i = nPos+1 ;
                    }
                }
                while (nPos != -1) ;
                query = begining + " " + end ;
                queryUpper = query.toUpperCase();
                n = queryUpper.indexOf("FROM", nStart);
            }
        }

        nStart = 0;
        if(typeOperation == SQLTypeOperation.Insert)
        {
            n = queryUpper.indexOf("INTO");
            if (n>=0)
            {
                nStart = n + 4;
            }
        }
        else if(typeOperation == SQLTypeOperation.Update)
        {
            nStart = 6;
        }
        else if(typeOperation == SQLTypeOperation.Delete)
        {
            n = queryUpper.indexOf("FROM");
            if (n>=0)
            {
                nStart = n + 4;
            }
        }
        else if(typeOperation == SQLTypeOperation.Create)
        {
            n = queryUpper.indexOf("TABLE");
            if (n >= 0)
            {
                nStart = n + 5;
            }
        }
        else if(typeOperation == SQLTypeOperation.Drop)
        {
            n = queryUpper.indexOf("TABLE");
            if (n >= 0)
            {
                nStart = n + 5;
            }
        }
        else if(typeOperation == SQLTypeOperation.Lock)
        {
            n = queryUpper.indexOf("TABLE");
            if (n >= 0)
            {
                nStart = n + 5;
            }
        }

        if (nStart == 0)
            return query;

        String left = query.substring(0, nStart);
        String right = query.substring(nStart);
        right = StringUtil.trimLeft(right);
        int nWhiteSpace = StringUtil.getNextWhiteSpacePosition(right);
        if(nWhiteSpace != -1)
        {
            String tableName = right.substring(0, nWhiteSpace);
            String remaining = right.substring(nWhiteSpace);
            tableName = setPrefixIfRequired(env, tableName, forcedReplacedPrefix);
            query = left + " " + tableName + " " +  remaining;
        }

        return query;
    }

    // Not prefixed table name are prefixed by env
    // Table names prefixed by csForcedReplacedPrefix are also prefixed by env
    // Table names prefixed by another prefix are unchanged
    public static String addEnvironmentPrefix(String env, String csQuery, SQLTypeOperation typeOperation, String csForcedReplacedPrefix)
    {
        if (env.equals(""))
        {
            return csQuery;
        }

        if (csQuery.startsWith("_"))
        {
            return csQuery.substring(1);
        }

        String queryUpper = csQuery.toUpperCase();

        if(queryUpper.indexOf(" JOIN ") == -1)  // No JOIN keyword : Use standard parser
            return addEnvironmentPrefixStandardParser(env, csQuery, queryUpper, typeOperation, csForcedReplacedPrefix, false);

        // Custom parser for join keyword support
        int nStart = 0 ;
        int nPosSelect = queryUpper.indexOf("SELECT");
        if (nPosSelect >= 0)
        {
            nStart = nPosSelect + 7;
            int nPosFrom = queryUpper.indexOf("FROM", nStart);
            if(nPosFrom >= 0)   // Only 1 FROM keyword in a SELECT clause //while (n>=0)
            {
                nStart = nPosFrom + 5;
                String begining = csQuery.substring(0, nStart) ;
                String querry = csQuery.substring(nStart) ;

                int nEnd = getPositionFirstStopListKeywordNextFrom(queryUpper, nStart);
                String end = "" ;
                if (nEnd != -1)
                {
                    end = csQuery.substring(nEnd) ;
                    querry = csQuery.substring(nStart, nEnd) ;
                }

                begining = addLeadingTablePrefix(begining, env, csForcedReplacedPrefix, querry);
                csQuery = begining + " " + end ;
                queryUpper = csQuery.toUpperCase();
                //n = csQueryUpper.indexOf("FROM", nStart);
            }

            int nPosJoin = queryUpper.indexOf("JOIN", nStart);
            if(nPosJoin >= 0)
            {
                while(nPosJoin >= 0)
                {
                    nStart = nPosJoin + 4;
                    String left = csQuery.substring(0, nStart) ;

                    String right = csQuery.substring(nStart) ;
                    String rightUpper = queryUpper.substring(nStart) ;
                    int nEnd = rightUpper.indexOf(" ON ", 0) ;
                    if (nEnd != -1)
                    {
                        String tableName = StringUtil.getFirstWord(right);
                        right = StringUtil.removeFirstWord(right);
                        tableName = setPrefixIfRequired(env, tableName, csForcedReplacedPrefix);

                        csQuery = left + " " + tableName + " " + right;
                        queryUpper = csQuery.toUpperCase();

                        nPosJoin = queryUpper.indexOf("JOIN", nStart);
                    }
                }
            }
            queryUpper = csQuery.toUpperCase();
            csQuery = addEnvironmentPrefixStandardParser(env, csQuery, queryUpper, typeOperation, csForcedReplacedPrefix, true);
            return csQuery;
        }

        nStart = 0;
        int n;
        if(typeOperation == SQLTypeOperation.Insert)
        {
            n = queryUpper.indexOf("INTO");
            if (n>=0)
            {
                nStart = n + 4;
            }
        }
        else if(typeOperation == SQLTypeOperation.Update)
        {
            nStart = 6;
        }
        else if(typeOperation == SQLTypeOperation.Delete)
        {
            n = queryUpper.indexOf("FROM");
            if (n>=0)
            {
                nStart = n + 4;
            }
        }
        else if(typeOperation == SQLTypeOperation.Create)
        {
            n = queryUpper.indexOf("TABLE");
            if (n >= 0)
            {
                nStart = n + 5;
            }
        }
        else if(typeOperation == SQLTypeOperation.Drop)
        {
            n = queryUpper.indexOf("TABLE");
            if (n >= 0)
            {
                nStart = n + 5;
            }
        }
        else if(typeOperation == SQLTypeOperation.Lock)
        {
            n = queryUpper.indexOf("TABLE");
            if (n >= 0)
            {
                nStart = n + 5;
            }
        }

        if (nStart == 0)
            return csQuery;

        String left = csQuery.substring(0, nStart);
        String right = csQuery.substring(nStart);
        right = StringUtil.trimLeft(right);
        int nWhiteSpace = StringUtil.getNextWhiteSpacePosition(right);
        if(nWhiteSpace != -1)
        {
            String tableName = right.substring(0, nWhiteSpace);
            String remaining = right.substring(nWhiteSpace);
            tableName = setPrefixIfRequired(env, tableName, csForcedReplacedPrefix);
            csQuery = left + " " + tableName + " " +  remaining;
        }

        return csQuery;
    }

    private static String setPrefixIfRequired(String env, String right, String forcedReplacedPrefix)
    {
        if(right.indexOf('.') == -1)    // Prefix not already set
            return env + "." + right;
        if(!StringUtil.isEmpty(forcedReplacedPrefix))
        {
            if(StringUtil.startsWithNoCase(right, forcedReplacedPrefix))    // table prefix is a prefix that must be replaced
            {
                int nForcedReplacedPrefixLength = forcedReplacedPrefix.length();
                String table = right.substring(nForcedReplacedPrefixLength);
                return env + "." + table;
            }
        }
        return right;
    }

    public static String updateMarkers(String csQuery)
    {
        int nPosStart = csQuery.indexOf('#', 0);
        while (nPosStart != -1)
        {
            String left = csQuery.substring(0, nPosStart);
            int n = nPosStart;
            n++; // Skip the #
            int nLength = csQuery.length();
            char c = csQuery.charAt(n);
            while (Character.isLetterOrDigit(c))
            {
                n++;
                if (n == nLength)
                {
                    break;
                }
                c = csQuery.charAt(n);
            }
            String itemId = csQuery.substring(nPosStart, n);
            if (itemId != null)
            {
                nPosStart += itemId.length();
                String right = csQuery.substring(nPosStart);
                csQuery = left + "?" + right;
            }

            nPosStart = csQuery.indexOf('#', nPosStart);
        }
        return csQuery;
    }
}
