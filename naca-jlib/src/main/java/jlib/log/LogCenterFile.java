/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;


/*
 * Created on 3 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import jlib.misc.FileSystem;
import jlib.misc.StringUtil;
import jlib.misc.Time_ms;
import jlib.xml.Tag;



/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LogCenterFile extends LogCenter
{
	public LogCenterFile(LogCenterLoader logCenterLoader)
	{
		super(logCenterLoader);
	}
	
	public void loadSpecificsEntries(Tag tagLogCenter)	// Special values for file appenders
	{
		csFormat = tagLogCenter.getVal("Format");
		String csFileStrategy = tagLogCenter.getVal("FileStrategy");
		String csFilePath = tagLogCenter.getVal("FilePath");
		csFilePath = FileSystem.normalizePath(csFilePath);
		String csFileName = tagLogCenter.getVal("FileName");
		
		csFile = FileSystem.buildFileName(csFilePath, csFileName, null);
		FileSystem.createPath(csFile);
		
		if(csFileStrategy.equalsIgnoreCase("Append"))
			bAppend = true;
		else if(csFileStrategy.equalsIgnoreCase("BackupOnstart"))	// Backup On Start
		{
			bAppend = false;
			
			// Read the backup strategy tag
			Tag tagBackup = tagLogCenter.getChild("Backup");
			if(tagBackup != null)
			{
				String csBackupPath = tagBackup.getVal("BackupPath");
				csBackupPath = FileSystem.normalizePath(csBackupPath);
				if(csBackupPath.length() > 0 && csBackupPath.startsWith("."))	// Relative to csFilePath
					csBackupPath = csFilePath + csBackupPath;
				csBackupPath = FileSystem.normalizePath(csBackupPath);				
				FileSystem.createPath(csBackupPath);
				
				String csBackupFileFormat = tagBackup.getVal("BackupFileFormat");
				csBackupFileFormat = normalizeBackupFileFormat(csBackupFileFormat);
				
				String csBackupFile = FileSystem.buildFileName(csBackupPath, csBackupFileFormat, null);
				
				FileSystem.moveOrCopy(csFile, csBackupFile);
				
				int nMaxBackupFileCount = tagBackup.getValAsInt("MaxBackupFileCount");
				if(nMaxBackupFileCount >= 0)
					FileSystem.keepMoreRecentFile(csBackupPath, nMaxBackupFileCount);
			}
		}		
		else
			bAppend = false;
	}
	
	private String normalizeBackupFileFormat(String csBackupFileFormat)
	{
		if(csBackupFileFormat.indexOf("[BackupDateTime]") != -1)
		{
			String csDateTime = Time_ms.formatYYYYMMDDHHMMSS_ms(Time_ms.getCurrentTime_ms());
//			DateUtil dateUtil = new DateUtil();
//			String csDateTime = dateUtil.getCurrentDateTimeYYYYMMDD_HHMMSS();
			csBackupFileFormat = StringUtil.replace(csBackupFileFormat, "[BackupDateTime]", csDateTime, false);
		}
		return csBackupFileFormat;
	}
	
	boolean open()
	{
		try 
		{ 			
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(csFile, bAppend)));
		} 
		catch (Exception e) 
		{ 
			System.err.println ("Error writing to file"); 
			return false;
		}  
		return true;
	}
	
	boolean closeLogCenter()
	{
		printWriter.close();
		return true;
	}
	
		
	void preSendOutput()
	{
	}
	
	void sendOutput(LogParams logParam)
	{
		if(printWriter != null)
		{
			int nNbLoops = patternLayout.getNbLoop(logParam);
			for(int n=0; n<nNbLoops; n++)
			{
				String csOut = patternLayout.format(logParam, n);
				printWriter.print(csOut);
			}
		}
	}	
		
	void postSendOutput()
	{
		if(printWriter != null)
			printWriter.flush();
	}
	
	String getFormat()
	{
		return csFormat;
	}
		
	private String csFile = null;
	private boolean bAppend  = false;
	
	private PrintWriter printWriter = null;
	
	private String csFormat = null;
	
	public String getType()
	{
		return "LogCenterFile";
	}
}
