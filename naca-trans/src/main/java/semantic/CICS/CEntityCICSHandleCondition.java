/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.CICS;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

import java.util.ArrayList;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCICSHandleCondition extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSHandleCondition(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void HandleCondition(String cond, String label)
	{
		handledConditionLabels.add(label);
		handledConditions.add(cond);
	}
	public void UnhandleCondition(String cond)
	{
		unhandledConditions.add(cond);
	}
	
	protected ArrayList<String> handledConditions = new ArrayList<String>();
	protected ArrayList<String> unhandledConditions = new ArrayList<String>();
	protected ArrayList<String> handledConditionLabels = new ArrayList<String>();
	public boolean ignore()
	{
		return false; 
	}
}
