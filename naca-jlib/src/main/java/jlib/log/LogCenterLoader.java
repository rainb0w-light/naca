/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import jlib.xml.Tag;

/**
 * Reads the <i>LogCenter</i> tag  from a JLib.log xml configuration file, 
 * creates a new instance of {@link LogCenter}, and registers it to the {@link Log}
 * static collection.
 * 
 * @author PJD
 */
public class LogCenterLoader
{
/**
 * The default class constructor, to instantiate a new {@link LogCenterLoader}.
 */
	public LogCenterLoader()
	{
	}

/**
 * Reads a <i>LogCenter</i> tag, and instantiates, initializes and registers
 * the corresponding {@link LogCenter} to the {@link Log} static collection.
 * @param tagLogCenter The JLib.log xml configuration file should be read
 * with a {@link Tag} object. Then the <i>LogCenter</i> tag should be located,
 * and provided here.
 * @return <i>true</i> if the <i>LogCenter</i> tag has been successfully
 * processed.
 */
	boolean loadDefinition(Tag tagLogCenter)
	{
		csChannel = tagLogCenter.getVal("Channel");
		isenable = tagLogCenter.getValAsBoolean("Enable");
		
		nNbRequestBufferSize = tagLogCenter.getValAsInt("NbRequestBufferSize");
		isasynchronous = tagLogCenter.getValAsBoolean("Asynchronous");
		
		String csLevel = tagLogCenter.getVal("Level");
		String csFlow = tagLogCenter.getVal("Flow");		
		
		logLevel  = LogLevel.getLevel(csLevel);
		logFlow = LogFlow.getNamedFlow(csFlow);
		
		LogCenter logCenter = createLogCenter(tagLogCenter);
		if(logCenter != null)
		{
			Log.registerLogCenter(logCenter);
			return true;
		}	

		return false;			
	}
	boolean saveDefinition(Tag tagLogCenter)
	{
		tagLogCenter.addVal("Name", csName);
		tagLogCenter.addVal("NbRequestBufferSize", nNbRequestBufferSize);
		tagLogCenter.addVal("Asynchronous", isasynchronous);
		tagLogCenter.addVal("Enable", isenable);
		tagLogCenter.addVal("Mode", csMode);
		tagLogCenter.addVal("Channel", csChannel);
		tagLogCenter.addVal("Level", logLevel.getAsString());
		tagLogCenter.addVal("Flow", LogFlow.getFlow(logFlow));
		
		return true;
	}
	
	
	private LogCenter createLogCenter(Tag tagLogCenter)
	{
		csName = tagLogCenter.getVal("Name");
		csMode = tagLogCenter.getVal("Mode");
		if(csMode.equalsIgnoreCase("FileST6"))
		{
			PatternLayoutST6 layout = new PatternLayoutST6();
			LogCenterFile logCenter = new LogCenterFile(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			logCenter.setPatternLayout(layout);
			return logCenter;
		}
		else if(csMode.equalsIgnoreCase("Console"))
		{
			LogCenterConsole logCenter = new LogCenterConsole(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			PatternLayoutConsole layout = new PatternLayoutConsole(logCenter.getFormat());
			logCenter.setPatternLayout(layout);
			return logCenter;
		}
		else if(csMode.equalsIgnoreCase("Db"))
		{
			PatternLayoutDb layout = new PatternLayoutDb();
			LogCenterDb logCenter = new LogCenterDb(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			logCenter.setPatternLayout(layout);
			return logCenter;
		}	
		else if(csMode.equalsIgnoreCase("DbFlat"))
		{
			PatternLayoutDb layout = new PatternLayoutDb();
			LogCenterDbFlat logCenter = new LogCenterDbFlat(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			logCenter.setPatternLayout(layout);
			return logCenter;
		}
		else if(csMode.equalsIgnoreCase("FileRawLine"))
		{
			LogCenterFile logCenter = new LogCenterFile(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			PatternLayoutRawLine layout = new PatternLayoutRawLine(logCenter.getFormat());
			logCenter.setPatternLayout(layout);
			return logCenter;
		}
		else if(csMode.equalsIgnoreCase("FileSTCheck"))
		{
			PatternLayoutSTCheck layout = new PatternLayoutSTCheck();
			LogCenterFile logCenter = new LogCenterFile(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			logCenter.setPatternLayout(layout);
			return logCenter;
		}
		else if(csMode.equalsIgnoreCase("FileChunk"))
		{
			PatternLayoutFileChunk layout = new PatternLayoutFileChunk();
			LogCenterFile logCenter = new LogCenterFile(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			logCenter.setPatternLayout(layout);
			return logCenter;
		}	
		else if(csMode.equalsIgnoreCase("PluginConsole"))
		{
			LogCenterPluginConsole logCenter = new LogCenterPluginConsole(this);
			logCenter.loadSpecificsEntries(tagLogCenter);
			PatternLayoutConsole layout = new PatternLayoutConsole(logCenter.getFormat());
			logCenter.setPatternLayout(layout);
			return logCenter;
		}
		return null;
	}
	
	public boolean isEnable()
	{
		return isenable;
	}

	public String getChannel()
	{
		return csChannel;
	}

	public String getMode()
	{
		return csMode;
	}

	public LogLevel getLogLevel()
	{
		return logLevel;
	}
	public LogFlow getFlow()
	{
		return logFlow;
	}

	public int getNbRequestBufferSize()
	{
		return nNbRequestBufferSize;
	}

	public boolean getAsynchronous()
	{
		return isasynchronous;
	}
	
	protected boolean isenable = true;
	protected int nNbRequestBufferSize = 0;
	protected boolean isasynchronous = false;
	protected String csChannel = null;			
	protected LogLevel logLevel = null;
	protected String csName = "";
	protected String csMode = "";
	protected LogFlow logFlow = null;
}
