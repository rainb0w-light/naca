/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import generate.CBaseLanguageExporter;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseDataReference;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CEntityNoAction;
import semantic.Verbs.CEntityInitialize;
import semantic.Verbs.CEntitySetConstant;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityFormAccessor extends CBaseDataReference
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param out
	 * @param type
	 * @param owner
	 */
	public CEntityFormAccessor(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, CEntityResourceForm owner)
	{
		super(l, name, cat, out);
		owner = owner ;
		reference = owner ;
		parent = owner ;
	}
	public CEntityResourceForm GetForm()
	{
		return owner ;
	}
	protected CEntityResourceForm owner = null ;
	public void Clear()
	{
		super.Clear();
		owner = null ;
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String value = term.GetValue() ;
		CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eAssign.SetToZero(owner) ;
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eAssign.SetToSpace(owner) ;
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			CEntityInitialize init = factory.NewEntityInitialize(l, owner);
			owner.RegisterWritingAction(init);
			return init ;
			//eAssign.SetToLowValue(owner) ;
		}
		else
		{
			return null ;
		}
		owner.RegisterWritingAction(eAssign);
		return eAssign ;
	}
	public boolean ignore()
	{
		return false ;
	}
	
	protected boolean isvirtual = false ;
	public void setVirtual()
	{
		isvirtual = true ;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		if (isvirtual)
		{
			return CDataEntityType.VIRTUAL_FORM ;
		}
		else
		{
			return CDataEntityType.FORM ;
		}
	}
	public String GetConstantValue()
	{
		return "" ;
	} 	 
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		if (term.GetDataType() == CDataEntityType.FORM && !owner.IsSaveCopy())
		{
			CEntityNoAction act = factory.NewEntityNoAction(l) ; 
			factory.programCatalog.RegisterMapCopy(act) ;
			return act ;
		}
		else
		{
			return null;
		}
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var, boolean bRead)
	{
		boolean b = super.ReplaceVariable(field, var, bRead) ;
		if (field == owner)
		{
			owner = (CEntityResourceForm)var ;
			if (bRead)
			{
				var.RegisterReadReference(this) ;
				field.UnRegisterReadReference(this) ;
			}
			else
			{
				var.RegisterWriteReference(this) ;
				field.UnRegisterWriteReference(this) ;
			}
			return true ;
		}		
		return b ;
	}
	public CEntityResourceForm getSaveCopy()
	{
		return owner.getSaveCopy() ;
	}
//	protected void RegisterMySelfToCatalog()
//	{
//		programCatalog.RegisterDataEntity(GetName(), this) ;
//		programCatalog.RegisterDataEntity("S" + GetName(), this) ;
//	}
}
