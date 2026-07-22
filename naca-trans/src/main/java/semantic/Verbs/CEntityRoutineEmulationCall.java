/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 17 janv. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.List;
import semantic.CDataEntity;

import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityRoutineEmulationCall extends CBaseActionEntity
{

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int pos = parameters.indexOf(field) ;
		if (pos >= 0)
		{
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			parameters.set(pos, var) ;
			return true ;
		}
		return false ;
	}
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityRoutineEmulationCall(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	protected String csDisplay = "" ;
	protected Vector<CDataEntity> parameters = new Vector<CDataEntity>() ;
	public void Clear()
	{
		super.Clear() ;
		parameters.clear() ;
	}
	public void SetDisplay(String disp)
	{
		csDisplay = disp ;
	}
	public void AddParameter(CDataEntity e)
	{
		parameters.add(e) ;
	}
	public String getRoutineName() {
		return csDisplay;
	}
	public boolean isDynamicAllocation() {
		return "tools.dynamicAllocation".equals(csDisplay);
	}
	public List<CDataEntity> getEmulationParameters() {
		List<CDataEntity> result = new ArrayList<>();
		for (CDataEntity e : parameters) {
			if (e != null && !e.ignore()) {
				result.add(e);
			}
		}
		return result;
	}
}
