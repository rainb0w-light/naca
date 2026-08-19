/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

import nacaLib.base.CJMapObject;
import nacaLib.exceptions.CGotoException;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;

/**
 * @author U930DI
 *
 */
public class SQLErrorManager extends CJMapObject
{
    /** Creates a new sqlerror manager instance. */
    public SQLErrorManager()
    {
    }

    /** Executes the reuse operation. */
    public void reuse()
    {
        sectionErrorGoto = null;
        paragraphErrorGoto = null;
        qLErrorGotoContinueType = null;
    }

    /** Executes the manage on error goto operation. */
    public void manageOnErrorGoto(Paragraph paragraphSQGErrorGoto, CSQLStatus sqlStatus)
    {
        registerOnErrorGoto(paragraphSQGErrorGoto);
        manageSQLError(sqlStatus);
    }

    /** Executes the manage on error goto operation. */
    public void manageOnErrorGoto(Section section, CSQLStatus sqlStatus)
    {
        registerOnErrorGoto(section);
        manageSQLError(sqlStatus);
    }

    /** Executes the manage on error continue operation. */
    public void manageOnErrorContinue(CSQLStatus sqlStatus)
    {
        registerOnErrorContinue();
        manageSQLError(sqlStatus);
    }




    private void registerOnErrorGoto(Section section)
    {
        sectionErrorGoto = section;
        paragraphErrorGoto = null;
        qLErrorGotoContinueType = SQLErrorGotoContinueType.OnErrorGoto;
    }

    private void registerOnErrorGoto(Paragraph paragraph)
    {
        sectionErrorGoto = null;
        paragraphErrorGoto = paragraph;
        qLErrorGotoContinueType = SQLErrorGotoContinueType.OnErrorGoto;
    }

    private void registerOnErrorContinue()
    {
        sectionErrorGoto = null;
        paragraphErrorGoto = null;
        qLErrorGotoContinueType = SQLErrorGotoContinueType.OnErrorContinue;
    }

    /** Executes the manage sqlerror operation. */
    public void manageSQLError(CSQLStatus sqlStatus)
    {
        if(sqlStatus != null)
        {
            boolean issQLCodeError = sqlStatus.isLastSQLCodeAnError();
            if(issQLCodeError)
            {
                if(qLErrorGotoContinueType == SQLErrorGotoContinueType.OnErrorGoto)
                {
                    if(paragraphErrorGoto != null)
                    {
                        CGotoException e = new CGotoException(paragraphErrorGoto);
                        throw e;
                    }
                    else if(sectionErrorGoto != null)
                    {
                        CGotoException e = new CGotoException(sectionErrorGoto);
                        throw e;
                    }
                }
                else if(qLErrorGotoContinueType == SQLErrorGotoContinueType.OnErrorContinue)
                {
                    // Intentionally empty.
                }
                else
                {
                    // Crash: TODO force shutdown app
                }
            }
        }
    }

    private Section sectionErrorGoto = null;
    private Paragraph paragraphErrorGoto = null;

    private SQLErrorGotoContinueType qLErrorGotoContinueType = null;
}
