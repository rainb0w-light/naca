/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;



/** Provides sqlclause spcall behavior. */
public class SQLClauseSPCall extends SQLClauseSPCallBase
{
    SQLClauseSPCall(String csName, boolean bCheckParams)
    {
        super(csName, bCheckParams);
    }

    // Parameters IN
    /** Executes the param in operation. */
    public SQLClauseSPCall paramIn(String csVal)
    {
        SQLClauseSPParamInString param = new SQLClauseSPParamInString(csVal);
        addParam(param);

        return this;
    }

    /** Executes the param in operation. */
    public SQLClauseSPCall paramIn(int nVal)
    {
        SQLClauseSPParamInInt param = new SQLClauseSPParamInInt(nVal);
        addParam(param);

        return this;
    }

    /** Executes the param in operation. */
    public SQLClauseSPCall paramIn(short sVal)
    {
        SQLClauseSPParamInShort param = new SQLClauseSPParamInShort(sVal);
        addParam(param);

        return this;
    }

    /** Executes the param in operation. */
    public SQLClauseSPCall paramIn(double dVal)
    {
        SQLClauseSPParamInDouble param = new SQLClauseSPParamInDouble(dVal);
        addParam(param);

        return this;
    }

    // Parameters IN-OUT
    /** Executes the param in out operation. */
    public SQLClauseSPCall paramInOut(String tcsVal[])
    {
        SQLClauseSPParamInOutString param = new SQLClauseSPParamInOutString(SQLClauseSPParamWay.InOut, tcsVal);
        addParam(param);

        return this;
    }

    /** Executes the param in out operation. */
    public SQLClauseSPCall paramInOut(int tnVal[])
    {
        SQLClauseSPParamInOutInt param = new SQLClauseSPParamInOutInt(SQLClauseSPParamWay.InOut, tnVal);
        addParam(param);

        return this;
    }

    /** Executes the param in out operation. */
    public SQLClauseSPCall paramInOut(double tdVal[])
    {
        SQLClauseSPParamInOutDouble param = new SQLClauseSPParamInOutDouble(SQLClauseSPParamWay.InOut, tdVal);
        addParam(param);

        return this;
    }

    // Parameters OUT
    /** Executes the param out operation. */
    public SQLClauseSPCall paramOut(String tcsVal[])
    {
        SQLClauseSPParamInOutString param = new SQLClauseSPParamInOutString(SQLClauseSPParamWay.Out, tcsVal);
        addParam(param);

        return this;
    }

    /** Executes the param out operation. */
    public SQLClauseSPCall paramOut(int tnVal[])
    {
        SQLClauseSPParamInOutInt param = new SQLClauseSPParamInOutInt(SQLClauseSPParamWay.Out, tnVal);
        addParam(param);

        return this;
    }

    /** Executes the param out operation. */
    public SQLClauseSPCall paramOut(double tdVal[])
    {
        SQLClauseSPParamInOutDouble param = new SQLClauseSPParamInOutDouble(SQLClauseSPParamWay.Out, tdVal);
        addParam(param);

        return this;
    }

//
//  void retrieveOutValues(PreparedCallableStatement preparedCallableStatement, CSQLStatus sqlStatus)
//  {
//      if(preparedCallableStatement != null)
//      {
//          for(int n=0; n<arrParamDesc.size(); n++)
//          {
//              StoredProcParamDesc paramDesc = arrParamDesc.get(n);
//              paramDesc.retrieveOutValues(n, preparedCallableStatement, sqlStatus);
//          }
//      }
//  }

}
