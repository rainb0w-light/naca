/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 4 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.CICS;

import java.util.Vector;

import java.util.ArrayList;


import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCICSAssign extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSAssign(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	
	public void AddRequest(String param, CDataEntity var)
	{
		parameters.add(param);
		arrVariables.add(var) ;
	}
	
	protected ArrayList<String> parameters = new ArrayList<String>() ;
	protected Vector<CDataEntity> arrVariables = new Vector<CDataEntity>() ;

	public boolean ignore()
	{
		boolean ignore = true ;
		for (int i = 0; i< parameters.size(); i++)
		{
			CDataEntity e = arrVariables.get(i);
			ignore &= e.ignore() ;
		}
		return ignore;
	}
	
	public void Clear()
	{
		super.Clear();
		arrVariables.clear() ;
	}

}
