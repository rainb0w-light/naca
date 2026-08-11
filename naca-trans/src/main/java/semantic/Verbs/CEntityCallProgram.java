/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;


import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.expression.CEntityString;
import utils.*;


/**
 * @author sly
 *
 */
public class CEntityCallProgram extends CBaseActionEntity
{
	/**
	 * @param cat
	 */
	public CEntityCallProgram(int l, CObjectCatalog cat, CDataEntity Reference)
	{
		super(l, cat);
		reference = Reference ;
		cat.RegisterCallProgram(this) ;
	}

	public void SetParameterByRef(CDataEntity e)
	{
		CCallParameter p = new CCallParameter(e, CCallParameterMethode.BY_REFERENCE);
		parameters.add(p);
	}
	public void SetParameterByContent(CDataEntity e)
	{
		CCallParameter p = new CCallParameter(e, CCallParameterMethode.BY_CONTENT);
		parameters.add(p);
	}
	public void SetParameterByValue(CDataEntity e)
	{
		CCallParameter p = new CCallParameter(e, CCallParameterMethode.BY_VALUE);
		parameters.add(p);
	}
	public void SetParameterLengthOf(CDataEntity e)
	{
		CCallParameter p = new CCallParameter(e, CCallParameterMethode.LENGTH_OF);
		parameters.add(p);
	}

	protected boolean ischecked = false ;
	protected CDataEntity reference = null ;
	protected CBaseLanguageEntity onErrorBloc ;
	protected Vector<CCallParameter> parameters = new Vector<CCallParameter>() ;
	public void Clear()
	{
		super.Clear() ;
		reference = null ;
		parameters.clear();
	}
	protected static class CCallParameterMethode
	{
		public static CCallParameterMethode BY_REFERENCE = new CCallParameterMethode();
		public static CCallParameterMethode BY_CONTENT = new CCallParameterMethode();
		public static CCallParameterMethode BY_VALUE = new CCallParameterMethode();
		public static CCallParameterMethode LENGTH_OF = new CCallParameterMethode();
	}
	protected class CCallParameter
	{
		public CCallParameter(CDataEntity e, CCallParameterMethode m)
		{
			reference = e ;
			methode = m ;
		}
		public CDataEntity reference ;
		public CCallParameterMethode methode ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (reference == data)
		{
			data.UnRegisterReadingAction(this) ;
			reference = null ;
			return true ;
		}
		for (int i = 0; i< parameters.size(); i++)
		{
			CCallParameter param = parameters.get(i) ;
			if (param.reference == data)
			{
				data.UnRegisterReadingAction(this) ;
				param.reference = null ;
				return true ;
			}
		}
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (reference == field)
		{
			reference = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		for (int i = 0; i< parameters.size(); i++)
		{
			CCallParameter param = parameters.get(i) ;
			if (param.reference == field)
			{
				param.reference = var ;
				field.UnRegisterReadingAction(this) ;
				var.RegisterReadingAction(this) ;
				return true ;
			}
		}
		return false ;
	}

	public CDataEntity getProgramReference()
	{
		return reference ;
	}

	public boolean isLiteralProgramReference()
	{
		return reference instanceof CEntityString;
	}

	public String getLiteralProgramName()
	{
		return isLiteralProgramReference()
			? ((CEntityString) reference).getLiteralValue()
			: null;
	}

	public boolean isChecked()
	{
		return ischecked;
	}

	public Vector<CCallParameterView> getCallParameters()
	{
		Vector<CCallParameterView> views = new Vector<CCallParameterView>();
		for (int i = 0; i < parameters.size(); i++)
		{
			CCallParameter p = parameters.get(i);
			if (!p.reference.ignore())
			{
				views.add(new CCallParameterView(getCallParameterMethod(p), p.reference));
			}
		}
		return views;
	}

	public boolean hasOnErrorBloc()
	{
		return onErrorBloc != null;
	}

	public CBaseLanguageEntity getOnErrorBloc()
	{
		return onErrorBloc;
	}

	private String getCallParameterMethod(CCallParameter p)
	{
		if (p.methode == CCallParameterMethode.BY_REFERENCE)
		{
			return "using";
		}
		else if (p.methode == CCallParameterMethode.LENGTH_OF)
		{
			return "usingLengthOf";
		}
		else if (p.methode == CCallParameterMethode.BY_VALUE)
		{
			return "usingValue";
		}
		else if (p.methode == CCallParameterMethode.BY_CONTENT)
		{
			return "usingContent";
		}
		return "using";
	}

	public static class CCallParameterView
	{
		private final String method;
		private final CDataEntity reference;

		public CCallParameterView(String method, CDataEntity reference)
		{
			this.method = method;
			this.reference = reference;
		}

		public String getMethod()
		{
			return method;
		}

		public CDataEntity getReference()
		{
			return reference;
		}
	}

	public void setChecked(boolean bChecked)
	{
		ischecked = bChecked ;
	}

	public void UpdateProgramReference(CDataEntity newProgram)
	{
		reference = newProgram ;
	}

	public void SetOnErrorBloc(CBaseLanguageEntity error)
	{
		onErrorBloc = error;
	}
}
