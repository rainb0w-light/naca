/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 22 févr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.sqlSupport;

import nacaLib.base.CJMapObject;
import nacaLib.exceptions.CGotoException;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class SQLErrorGotoContinueType
{
	public static SQLErrorGotoContinueType OnErrorGoto = new SQLErrorGotoContinueType(); 
	public static SQLErrorGotoContinueType OnErrorContinue = new SQLErrorGotoContinueType();
	
	SQLErrorGotoContinueType()
	{
	}
}

public class SQLErrorManager extends CJMapObject
{
	public SQLErrorManager()
	{
	}
	
	public void reuse()
	{
		sectionErrorGoto = null;
		paragraphErrorGoto = null;
		sQLErrorGotoContinueType = null;
	}
	
	public void manageOnErrorGoto(Paragraph paragraphSQGErrorGoto, CSQLStatus sqlStatus)
	{
		registerOnErrorGoto(paragraphSQGErrorGoto);
		manageSQLError(sqlStatus);
	}
	
	public void manageOnErrorGoto(Section section, CSQLStatus sqlStatus)
	{
		registerOnErrorGoto(section);
		manageSQLError(sqlStatus);
	}
	
	public void manageOnErrorContinue(CSQLStatus sqlStatus)
	{
		registerOnErrorContinue();
		manageSQLError(sqlStatus);
	}



	
	private void registerOnErrorGoto(Section section)
	{
		sectionErrorGoto = section;
		paragraphErrorGoto = null;
		sQLErrorGotoContinueType = SQLErrorGotoContinueType.OnErrorGoto;
	}

	private void registerOnErrorGoto(Paragraph paragraph)
	{
		sectionErrorGoto = null;
		paragraphErrorGoto = paragraph;
		sQLErrorGotoContinueType = SQLErrorGotoContinueType.OnErrorGoto;
	}
	
	private void registerOnErrorContinue()
	{
		sectionErrorGoto = null;
		paragraphErrorGoto = null;
		sQLErrorGotoContinueType = SQLErrorGotoContinueType.OnErrorContinue;
	}
	
	public void manageSQLError(CSQLStatus sqlStatus)
	{
		if(sqlStatus != null)
		{
			boolean bSQLCodeError = sqlStatus.isLastSQLCodeAnError();
			if(bSQLCodeError)
			{
				if(sQLErrorGotoContinueType == SQLErrorGotoContinueType.OnErrorGoto)
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
				else if(sQLErrorGotoContinueType == SQLErrorGotoContinueType.OnErrorContinue)
				{
					; // Do nothing
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
	
	private SQLErrorGotoContinueType sQLErrorGotoContinueType = null;
}
