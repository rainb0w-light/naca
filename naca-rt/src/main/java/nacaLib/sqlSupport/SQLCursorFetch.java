/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

/**
 * @author U930DI
 *
 */
public class SQLCursorFetch
{
//  public SQLCursorFetch(VarBufferPos Working, CSQLConnection SQLConnection, String csQuery)
//  {
//  }
    /** Creates a new sqlcursor fetch instance. */
    public SQLCursorFetch(boolean bOpen, SQL sql)
    {
        this.bOpen = bOpen;
        this.sQL = sql;
    }

//  public SQLCursorFetch fetch()
//  {
//      //if(bOpen && sQL != null)
//      {
//          // PJD ROWID Support:
//          /*
//          if(sQL.hasRowIdGenerated())
//          {
//              sqlItemRowId = new CSQLIntoItem();
//              sQL.into(sqlItemRowId);
//          }
//          */
//      }
//      return this;
//  }


    /** Executes the into operation. */
    public SQLCursorFetch into(VarAndEdit varInto)
    {
        if(bOpen && sQL != null)
        {
            sQL.into(varInto, null);
        }
        return this;
    }

    /** Executes the into operation. */
    public SQLCursorFetch into(Var varInto, Var varIndicator)
    {
        if (bOpen && sQL != null) {
            sQL.into(varInto, varIndicator);
        }
        return this;
    }

//  public String getCursorName()   // use for updatable cusrot that use Cursor Name
//  {
//      if(sQL != null)
//          return sQL.getCursorName();
//      return null;
//  }

    /** Executes the on error goto operation. */
    public SQLCursorFetch onErrorGoto(Paragraph paragraphSQGErrorGoto)
    {
        sQL.onErrorGoto(paragraphSQGErrorGoto);
        return this;
    }

    /** Executes the on error goto operation. */
    public SQLCursorFetch onErrorGoto(Section section)
    {
        sQL.onErrorGoto(section);
        return this;
    }

    /** Executes the on error continue operation. */
    public SQLCursorFetch onErrorContinue()
    {
        sQL.onErrorContinue();
        return this;
    }
    /** Executes the on warning goto operation. */
    public SQLCursorFetch onWarningGoto(Paragraph paragraphSQGErrorGoto)
    {
        // TODO
        return this;
    }

    /** Executes the on warning goto operation. */
    public SQLCursorFetch onWarningGoto(Section section)
    {
        // TODO
        return this;
    }

    /** Executes the on warning continue operation. */
    public SQLCursorFetch onWarningContinue()
    {
        // TODO
        return this;
    }


    // PJD ROWID Support:
    /*
    public ROWID getCurrentRowId()
    {
        if(sqlItemRowId != null)
            return sqlItemRowId.getRowId();
        return null;
    }
    */

    // PJD ROWID Support:private CSQLIntoItem sqlItemRowId = null;  // Used for updatable cursor that use RowId
    //protected SQL sQL = null;
    private boolean bOpen = false;
    public /*must be private */SQL sQL = null;
}
