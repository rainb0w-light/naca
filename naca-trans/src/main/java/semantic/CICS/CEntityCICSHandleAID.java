/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.CICS;


import java.util.ArrayList;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCICSHandleAID extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSHandleAID(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void HandleAID(String cond, String label)
	{
		m_arrHandledAIDLabels.add(label);
		m_arrHandledAIDs.add(cond);		
	}
	public void UnhandleAID(String cond)
	{
		m_arrUnhandledAIDs.add(cond);		
	}
	
	protected ArrayList<String> m_arrHandledAIDs = new ArrayList<String>();
	protected ArrayList<String> m_arrUnhandledAIDs = new ArrayList<String>();
	protected ArrayList<String> m_arrHandledAIDLabels = new ArrayList<String>();

	public boolean ignore()
	{
		if (m_arrHandledAIDs.size() == 0 && m_arrUnhandledAIDs.size() == 0)
		{
			return true;
		}
		return false ;
	}
}
