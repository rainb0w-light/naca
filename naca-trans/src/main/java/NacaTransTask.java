/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task ;

import utils.NacaTransLauncher;

/**
 * @author SLY
 *
 */
public class NacaTransTask extends Task
{
	protected String configfilePath = "" ;
	private String groupToTranscode = "" ;

	public void setConfig(String filename)
	{
		configfilePath = filename ;
	}

	public void execute() throws BuildException
	{
		super.getLocation() ;
		NacaTransLauncher obj = new NacaTransLauncher() ;
		try
		{
			obj.Start(configfilePath, groupToTranscode) ;
		}
		catch (Exception e)
		{
			e.printStackTrace() ;
			throw new BuildException() ;
		}
	}
	/**
	 * @param csGroupToTranscode The csGroupToTranscode to set.
	 */
	public void setGroupToTranscode(String csGroupToTranscode)
	{
		groupToTranscode = csGroupToTranscode;
	}
}
