/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 7 juil. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jlib.display;

import java.io.File;

import jlib.log.Log;
import jlib.xml.Tag;

/**
 * @author U930CV
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayConfig
{
	private DisplayConfig()
	{
	}
	private static DisplayConfig ms_Instance = null ;
	public static DisplayConfig getInstance()
	{
		if (ms_Instance == null)
		{
			ms_Instance = new DisplayConfig() ;
		}
		return ms_Instance ;
	}
	public void setRootPath(String path)
	{
		csRootPath = path.replace('\\', '/') ;
		if (!csRootPath.endsWith("/"))
		{
			csRootPath += '/' ;
		}
		System.out.println("JLIB DIsplay Config -> Root Path = "+csRootPath) ;
	}
	protected String csRootPath = "" ;
	public String getRootPath()
	{
		return csRootPath;
	}
	public void LoadConfig(String csINIFilePath)
	{
		Tag tagConfig = Tag.createFromFile(csINIFilePath) ;
		if  (tagConfig != null)
		{
			String csInitialDialogClass = tagConfig.getVal("InitialDialogFactory") ;
			try
			{
				factoryDialogs = (BaseDialogFactory) Class.forName(csInitialDialogClass).newInstance() ;
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				factoryDialogs = null;
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
				factoryDialogs = null;
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				factoryDialogs = null;
			}
			
			resourceManager = new ResourceManager() ;
			
			String csXSLFilePath = tagConfig.getVal("XSLFilePath") ;
			File xSLFilePath = new File(csRootPath + csXSLFilePath) ;
			if (!xSLFilePath.isFile())
			{
				xSLFilePath = null ;
			}
			resourceManager.setXSLFilePath("MAIN", xSLFilePath) ;

			String csLogINIFilePath = tagConfig.getVal("LogINIFilePath") ;
			if (csLogINIFilePath!=null && !csLogINIFilePath.equals(""))
			{
				csLogINIFilePath = csRootPath + csLogINIFilePath ;
				Log.open(csLogINIFilePath);
			}
			
			Tag tagFactory = tagConfig.getChild("factory") ;
			if (tagFactory != null)
			{
				factoryDialogs.Init(this, tagFactory) ;
			}
		}
	}
	
	private BaseDialogFactory factoryDialogs = null ;
	private ResourceManager resourceManager = null ;
	/**
	 * @return
	 */
	public BaseDialogFactory getDialogFactory()
	{
		return factoryDialogs ;
	}
	public ResourceManager getResourceManager()
	{
		return resourceManager ;
	}
}
