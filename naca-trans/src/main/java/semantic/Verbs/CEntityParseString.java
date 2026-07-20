/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

public class CEntityParseString extends CBaseActionEntity
{
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		boolean isreplace = false;
		if (variable == field)
		{
			variable = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			isreplace = true;
		}
		for (int i = 0; i < destinations.size(); i++)
		{
			CDataEntity[] entities = destinations.get(i);
			for (int j=0; j < entities.length; j++)
			{
				CDataEntity entity = entities[j];
				if (entity != null && entity == field)
				{
					entity = var ;
					field.UnRegisterReadingAction(this) ;
					var.RegisterReadingAction(this) ;
					isreplace = true;
				}
			}
		}
		if (delimitersMulti.contains(field))
		{
			int pos = delimitersMulti.indexOf(field) ;
			delimitersMulti.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			isreplace = true ;
		}
		if (delimitersSingle.contains(field))
		{
			int pos = delimitersSingle.indexOf(field) ;
			delimitersSingle.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			isreplace = true ;
		}
		if (tallying == field)
		{
			tallying = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			isreplace = true;
		}
		if (withPointer == field)
		{
			withPointer = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			isreplace = true;
		}
		return isreplace;
	}

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityParseString(int line,	CObjectCatalog cat)
	{
		super(line, cat);
	}

	public void ParseString(CDataEntity e)
	{
		variable = e ;
	}
	public void AddDelimiterSingle(CDataEntity e)
	{
		delimitersSingle.add(e);
	}
	public void AddDelimiterMulti(CDataEntity e)
	{
		delimitersMulti.add(e);
	}
	public void AddDestination(CDataEntity[] e)
	{
		destinations.add(e);
	}
	public void setTallying(CDataEntity e)
	{
		tallying = e;
	}
	public void setWithPointer(CDataEntity e)
	{
		withPointer = e;
	}
	protected CDataEntity variable = null ;
	protected Vector<CDataEntity[]> destinations = new Vector<CDataEntity[]>() ;
	protected Vector<CDataEntity> delimitersMulti = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> delimitersSingle = new Vector<CDataEntity>() ;
	protected CDataEntity tallying = null ;
	protected CDataEntity withPointer = null ;
	
	public void Clear()
	{
		super.Clear() ;
		variable = null ;
		delimitersMulti.clear() ;
		delimitersSingle.clear() ;
		destinations.clear();
	}
	public boolean ignore()
	{
		return variable.ignore();
	}

	public CDataEntity getVariable() {
		return variable;
	}
	public List<CDataEntity> getDelimitersSingle() {
		return delimitersSingle;
	}
	public List<CDataEntity> getDelimitersMulti() {
		return delimitersMulti;
	}
	public CDataEntity getWithPointer() {
		return withPointer;
	}
	public CDataEntity getTallying() {
		return tallying;
	}
	public boolean isHasOverflowHandler() {
		return !getActiveChildren().isEmpty();
	}
	public static class UnstringDestination {
		private final CDataEntity to;
		private final CDataEntity delimiterIn;
		private final CDataEntity countIn;
		public UnstringDestination(CDataEntity to, CDataEntity delimiterIn, CDataEntity countIn) {
			this.to = to;
			this.delimiterIn = delimiterIn;
			this.countIn = countIn;
		}
		public CDataEntity getTo() {
			return to;
		}
		public CDataEntity getDelimiterIn() {
			return delimiterIn;
		}
		public CDataEntity getCountIn() {
			return countIn;
		}
		public boolean isHasDelimiterOrCount() {
			return delimiterIn != null || countIn != null;
		}
	}
	public List<UnstringDestination> getUnstringDestinations() {
		List<UnstringDestination> result = new ArrayList<>();
		for (CDataEntity[] arr : destinations) {
			result.add(new UnstringDestination(arr[0], arr[1], arr[2]));
		}
		return result;
	}
}
