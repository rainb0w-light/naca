/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/**
 * 
 */
package utils;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: NacaTransLauncher.java,v 1.3 2007/12/06 07:24:07 u930bm Exp $
 */
public class NacaTransLauncher extends Transcoder
{
	public static void launchMain(String[] args)
	{
		String csCfg = "NacaTrans.cfg" ;
		boolean iscfgSet = false;
		String csGroupToTranscode = "" ;
		String csApplication = null;
		TranscoderAction transcoderAction = TranscoderAction.All;
		for (int nArg=0; nArg<args.length; nArg++)
		{
			String s = args[nArg];
			if ((s.startsWith("-") || s.startsWith("/")) && s.contains("="))
			{
				int eqPos = s.indexOf('=');
				String arg = s.substring(1, eqPos);
				String argUpper = arg.toUpperCase();
				String argValue = s.substring(eqPos + 1);

				if (argUpper.equals("APPLICATION"))
				{
					csApplication = argValue;
				}
				else if (argUpper.equals("GROUP"))
				{
					csGroupToTranscode = argValue;
				}
				else if (argUpper.equals("ACTION"))
				{
					String csAction = argValue;
					transcoderAction = getTranscoderAction(csAction);
				}
				else if (argUpper.equals("CONFIGFILE"))
				{
					csCfg = argValue;
					iscfgSet = true;
				}
			}
			else
			{
				if(!iscfgSet)
				{
					csCfg = s;
					iscfgSet = true;
				}
				else
					csGroupToTranscode = s;
			}
		}
		doStart(csApplication, transcoderAction, csCfg, csGroupToTranscode);
	}

	public static NacaTransLauncher doInitForPlugin(String configFilePath)
	{
		NacaTransLauncher transLauncher = new NacaTransLauncher() ;
		transLauncher.initForPlugin(configFilePath);
		return transLauncher;		
	}

	public static void doStart(String csApplication, TranscoderAction transcoderAction, String csCfg, String csGroupToTranscode)
	{
		NacaTransLauncher obj = new NacaTransLauncher() ;
		obj.setTranscoderAction(transcoderAction);

		obj.Start(csCfg, csGroupToTranscode) ;
	}

}
