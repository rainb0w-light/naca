/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.CICS;

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
public abstract class CEntityCICSDeQ extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCICSDeQ(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
	}
	public void SetResource(CDataEntity eRes, CDataEntity eLen)
	{
		resource = eRes ;
		length = eLen ;
	}
	
	protected CDataEntity resource = null	;
	protected CDataEntity length = null ;

	public boolean ignore()
	{
		return false ;
	}
	public void Clear()
	{
		super.Clear();
		resource = null ;
		length = null ;
	}

}
