/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 9 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


import parser.expression.CExpression;
import parser.expression.CTerminal;
import semantic.Verbs.CEntitySetConstant;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityIsNamedCondition;
import semantic.expression.CUnitaryEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityNamedCondition extends CDataEntity
{


	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntityNamedCondition(int l, String name, CObjectCatalog cat)
	{
		super(l, name, cat);
	}

//	public void SetCondition(CBaseEntityCondition cond)
//	{
//		condition = cond ;
//	}
	
//	protected CBaseEntityCondition condition = null ;

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String cs = term.GetValue() ;
		if (cs.equalsIgnoreCase("TRUE"))
		{
			CEntitySetConstant eSet = factory.NewEntitySetConstant(l);
			eSet.SetCondition(this, true);
			return eSet ;
		}
		else if (cs.equalsIgnoreCase("FALSE"))
		{
			CEntitySetConstant eSet = factory.NewEntitySetConstant(l);
			eSet.SetCondition(this, false);
			return eSet ;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
	 */
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null;
	}
	public CUnitaryEntityCondition GetAssociatedCondition(CBaseEntityFactory factory)
	{
		CEntityIsNamedCondition eCond = factory.NewEntityIsNamedCondition() ; 
		eCond.SetCondition(this) ;
		return eCond ;
	}
	
	public void AddInterval(CDataEntity eStart, CDataEntity eEnd)
	{
		endIntervals.add(eEnd);
		startIntervals.add(eStart);
	}
	public void AddValue(CDataEntity eValue)
	{
		values.add(eValue);
	}
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory) 
	{
		CEntityArrayReference e = factory.NewEntityArrayReference(getLine()) ;
		e.SetReference(this) ;
		for (int i=0; i<v.size(); i++)
		{
			CExpression expr = (CExpression)v.get(i);
			CBaseEntityExpression exp = expr.AnalyseExpression(factory);
			e.AddIndex(exp);
		}
		return e ;
	};
	
	protected Vector<CDataEntity> startIntervals = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> endIntervals = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> values = new Vector<CDataEntity>() ;
	public boolean ignore()
	{
		return startIntervals.size() == 0 && endIntervals.size() == 0 && values.size() == 0 ;
	}
	public String GetConstantValue()
	{
		return "" ;
	} 	 

	public boolean HasAccessors()
	{
		return false;
	}

	public boolean isValNeeded()
	{
		return false;
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.CONDITION;
	}
	public void Clear()
	{
		super.Clear();
		startIntervals.clear() ;
		endIntervals.clear() ;
		values.clear() ;
	}

	/**
	 * Target-agnostic read-only view of the level-88 single values. Each element
	 * is a semantic data entity (literal, constant or reference); the backend
	 * renders it in the REFERENCE role.
	 */
	public List<CDataEntity> getValues()
	{
		return Collections.unmodifiableList(values);
	}

	public static final class ValueModel
	{
		private final CDataEntity value;

		public ValueModel(CDataEntity value)
		{
			this.value = value;
		}

		public boolean isDefined()
		{
			return value != null;
		}

		public CDataEntity getValue()
		{
			return value;
		}
	}

	public List<ValueModel> getValueModels()
	{
		List<ValueModel> models = new ArrayList<>(values.size());
		for (CDataEntity value : values)
		{
			models.add(new ValueModel(value));
		}
		return Collections.unmodifiableList(models);
	}

	/**
	 * A single level-88 interval endpoint pair (start THROUGH end). Both
	 * endpoints are semantic data entities rendered in the REFERENCE role.
	 */
	public static final class IntervalModel
	{
		private final CDataEntity start;
		private final CDataEntity end;

		public IntervalModel(CDataEntity start, CDataEntity end)
		{
			this.start = start;
			this.end = end;
		}

		public CDataEntity getStart()
		{
			return start;
		}

		public CDataEntity getEnd()
		{
			return end;
		}
	}

	/**
	 * Target-agnostic read-only view of the level-88 intervals, pairing each
	 * start endpoint with its matching end endpoint.
	 */
	public List<IntervalModel> getIntervals()
	{
		int count = Math.min(startIntervals.size(), endIntervals.size());
		List<IntervalModel> intervals = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			intervals.add(new IntervalModel(startIntervals.get(i), endIntervals.get(i)));
		}
		return Collections.unmodifiableList(intervals);
	}

}
