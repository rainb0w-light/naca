/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package jlib.sqlColType;

import java.sql.Timestamp;

import jlib.misc.NumberParser;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLColTypeDate.java,v 1.3 2007/09/26 08:41:52 u930gn Exp $
 */
public class SQLColTypeDate
{
    private Timestamp ts = null;
    private boolean isvalid = false;

    /** Creates a new sqlcol type date instance. */
    public SQLColTypeDate(String csYYYYMMDD)
    {
        int nYYYY = NumberParser.getAsInt(csYYYYMMDD.substring(0, 4));
        int nMM = NumberParser.getAsInt(csYYYYMMDD.substring(4, 6));
        int nDD = NumberParser.getAsInt(csYYYYMMDD.substring(6, 8));

        ts = new Timestamp(nYYYY, nMM, nDD, 0, 0, 0, 0);
        isvalid = true;
    }

    /** Creates a new sqlcol type date instance. */
    public SQLColTypeDate(int nYYYYMMDD)
    {
        String csYYYYMMDD = "" + nYYYYMMDD;
        int nYYYY = NumberParser.getAsInt(csYYYYMMDD.substring(0, 4));
        int nMM = NumberParser.getAsInt(csYYYYMMDD.substring(4, 6));
        int nDD = NumberParser.getAsInt(csYYYYMMDD.substring(6, 8));

        ts = new Timestamp(nYYYY, nMM, nDD, 0, 0, 0, 0);
        isvalid = true;
    }

    /** Sets the infinite. */
    public void setInfinite()
    {
        ts = new Timestamp(2038, 12, 31, 0, 0, 0, 0);
        isvalid = true;
    }

    public boolean isValid()
    {
        return isvalid;
    }

    public Timestamp getTimeStamp()
    {
        return ts;
    }
}
