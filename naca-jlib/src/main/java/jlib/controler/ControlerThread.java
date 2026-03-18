/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.controler;


import java.util.Date;

import jlib.log.stdEvents.EventRemark;
import jlib.log.stdEvents.StdInfo;




public class ControlerThread extends Thread
{
	private BaseControler controler = null ;
	private BaseControlerTaskConfig grpConfig =  null ;
	private int nCurrentSite = -1 ;
	private boolean bDoAllSites = false ;
	private String csControlerName = "" ;
	private boolean bForceStarting = false ;
	private boolean bStopASAP = false ; 	   //Un flag qui permet d'arrêter le crawl en urgence.
	
	public ControlerThread(BaseControler ctrl)
	{
		controler = ctrl ;
		grpConfig = ctrl.getTaskConfig() ;
		csControlerName = grpConfig.getName() ;
	}

	public void AutoStart(int nStepId)
	{
		bForceStarting = false ;
		DoStart(nStepId) ;
	}

	public void StopControler(boolean bForce)
	{
		if (nCurrentSite>=0)
		{
			controler.setStatus(nCurrentSite, "STOPPING...") ;
		}
		controler.Stop(bForce) ;
		State st = this.getState() ;
		if (st == State.TIMED_WAITING || st == State.WAITING)
		{
			this.interrupt() ;
		}
		try
		{
			this.join() ;
		}
		catch (InterruptedException e)
		{
		}
		if (nCurrentSite>=0)
		{
			controler.setStatus(nCurrentSite, "NONE : Interrupted") ;
		}
	}

	private void DoStart(int nStepId)
	{
		if (grpConfig.isModeGroup() || nStepId == -1 || (grpConfig.getNbSteps()==1 && nStepId==0))
		{
			nCurrentSite = 0 ;
			bDoAllSites = true ;
		}
		else
		{
			nCurrentSite = nStepId ;
			bDoAllSites = false ;
		}
		start() ;
	}
	public void StartControler(int nStepId)
	{
		bForceStarting = true ;
		DoStart(nStepId) ;
	}

	//	..............................................................................................................
	/*
	 * Méthode principale du thread.
	 * Cette méthode vérifie l'état dans lequel se trouve le crawling et agit en conséquence.
	 */
	public void run() 
	{
		if (nCurrentSite<0 && nCurrentSite >= grpConfig.getNbSteps())
		{
			return ;
		}
//		grpConfig.setCurrentControler(controler) ;
		doRun() ;
//		grpConfig.setCurrentControler(null) ;
		
	}
	private void doRun()
	{
		int nSite = nCurrentSite ;

		boolean bAlreadyRun = false ;
		boolean bContinue = true ;
		while(bContinue)
		{
			if ((!bForceStarting || bAlreadyRun) && bDoAllSites)
			{
				Date dtGrpEnds = controler.getDateGroupEnds() ;
				
				if (dtGrpEnds == null)
				{
					if (grpConfig.getDelayBeforeStart() > 0)
					{
						StdInfo.log(grpConfig.getLogChannel(), grpConfig.getName(), "Waiting to start") ; 
						controler.setStatus(nCurrentSite, "NONE : Waiting to start") ;
						try
						{
							Thread.sleep(grpConfig.getDelayBeforeStart() * 1000) ;
						}
						catch (InterruptedException e1)
						{
							StdInfo.log(grpConfig.getLogChannel(), grpConfig.getName(), "Interrupted. Getting out") ; 
							controler.setStatus(nCurrentSite, "NONE : Interrupted") ;
							return ;
						}
					}
				}
				else
				{
					Date now = new Date() ;
					long msec = now.getTime() - dtGrpEnds.getTime() ;
					if (grpConfig.getDelayBeforeRestart()*1000 > msec)
					{
						StdInfo.log(grpConfig.getLogChannel(), grpConfig.getName(), "Waiting to restart") ; 
						controler.setStatus(nCurrentSite, "NONE : Waiting to restart") ;
						try
						{
							Thread.sleep(grpConfig.getDelayBeforeRestart()*1000 - msec) ;
						}
						catch (InterruptedException e1)
						{
							StdInfo.log(grpConfig.getLogChannel(), grpConfig.getName(), "Interrupted. Getting out") ; 
							controler.setStatus(nCurrentSite, "NONE : Interrupted") ;
							return ;
						}
					}
				}
			}

			bContinue &= !bStopASAP ;
			while (bContinue && nSite < grpConfig.getNbSteps())
			{
				nCurrentSite = nSite ;
				BaseControlerStepConfig stepConfig = grpConfig.getStep(nCurrentSite);
				String context = csControlerName ;
				if (!context.equals(""))
					context += "/" ;
				context += stepConfig.getName() ;
				if (!stepConfig.isActive())
				{
					controler.setStatus(nCurrentSite, "NONE : Inactive") ;
					StdInfo.log(grpConfig.getLogChannel(), context, "Site is INACTIVE") ; 
					bContinue = bDoAllSites ;
					nSite ++ ;
					continue ;
				}
	
				//if (!bForceStarting)
				//{
					Date dtStepEnds = controler.getDateStepEnds(nCurrentSite) ;
					
					if (dtStepEnds == null)
					{
						if (stepConfig.getDelayBeforeStart() < 0)
						{
							controler.setStatus(nCurrentSite, "NONE : Not started ") ;
							StdInfo.log(grpConfig.getLogChannel(), context, "Site is not Autostart") ; 
							bContinue = bDoAllSites ;
							nSite ++ ;
							continue ;
						}
						else if (stepConfig.getDelayBeforeStart() > 0)
						{
							StdInfo.log(grpConfig.getLogChannel(), context, "Waiting to start") ; 
							controler.setStatus(nCurrentSite, "NONE : Waiting to start") ;
							try
							{
								Thread.sleep(stepConfig.getDelayBeforeStart() * 1000) ;
							}
							catch (InterruptedException e1)
							{
								StdInfo.log(grpConfig.getLogChannel(), context, "Interrupted. Getting out") ; 
								controler.setStatus(nCurrentSite, "NONE : Interrupted") ;
								return ;
							}
						}
					}
					else
					{
						long msec = (new Date()).getTime() - dtStepEnds.getTime() ;
						
						if (stepConfig.getDelayBeforeRestart() < 0)
						{
							controler.setStatus(nCurrentSite, "NONE : Not started ") ;
							StdInfo.log(grpConfig.getLogChannel(), context, "Site is not Autostart") ; 
							bContinue = bDoAllSites ;
							nSite ++ ;
							continue ;
						}
						else if (stepConfig.getDelayBeforeRestart()*1000 > msec)
						{
							StdInfo.log(grpConfig.getLogChannel(), context, "Waiting to start") ; 
							controler.setStatus(nCurrentSite, "NONE : Waiting to start") ;
							
							
							try
							{
								Thread.sleep(stepConfig.getDelayBeforeRestart() * 1000 - msec) ;
							}
							catch (InterruptedException e1)
							{
								StdInfo.log(grpConfig.getLogChannel(), context, "Interrupted. Getting out") ; 
								controler.setStatus(nCurrentSite, "NONE : Interrupted") ;
								return ;
							}
						}
					}
				//}
			
				boolean bRet = controler.RunStep(nCurrentSite) ;

				if (!bRet) 
				{
					bContinue = false ;
				}
				else if (bStopASAP)
				{
					bContinue = false ;
				}
				else
				{
					bContinue = bDoAllSites ;
					nSite ++ ;
				}
				
				// Set start date for next site
				/*if (nCurrentSite < grpConfig.getNbSteps())
				{
					Date nextDate = new Date(new Date().getTime() + grpConfig.getStep(nCurrentSite + 1).getDelayBeforeRestart() * 1000);
					controler.setStartDate(nCurrentSite + 1, nextDate);
				}*/
				
			}
			bAlreadyRun = true ;
			nSite = 0 ;
			controler.setDateGroupEnds() ;
		}
	}

	public void StopControler(boolean bRestart, boolean bForce)
	{
		this.isDaemon() ;
		bStopASAP = !bRestart ;
		StopControler(bForce) ;
	}

	public void AutoStart()
	{
		AutoStart(-1) ;
	}

	public int getCurrentStep()
	{
		return nCurrentSite ;
	}	
}
