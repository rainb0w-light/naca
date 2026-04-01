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
		boolean bCfgSet = false;
		String csGroupToTranscode = "" ;
		String csApplication = null;
		TranscoderAction transcoderAction = TranscoderAction.All;
		for (int nArg=0; nArg<args.length; nArg++)
		{
			String s = args[nArg];
			if ((s.startsWith("-") || s.startsWith("/")) && s.contains("="))
			{
				int eqPos = s.indexOf('=');
				String sArg = s.substring(1, eqPos);
				String sArgUpper = sArg.toUpperCase();
				String sArgValue = s.substring(eqPos + 1);

				if (sArgUpper.equals("APPLICATION"))
				{
					csApplication = sArgValue;
				}
				else if (sArgUpper.equals("GROUP"))
				{
					csGroupToTranscode = sArgValue;
				}
				else if (sArgUpper.equals("ACTION"))
				{
					String csAction = sArgValue;
					transcoderAction = getTranscoderAction(csAction);
				}
				else if (sArgUpper.equals("CONFIGFILE"))
				{
					csCfg = sArgValue;
					bCfgSet = true;
				}
			}
			else
			{
				if(!bCfgSet)
				{
					csCfg = s;
					bCfgSet = true;
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
