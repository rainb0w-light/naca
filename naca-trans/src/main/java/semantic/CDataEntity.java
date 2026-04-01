/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import generate.*;

import java.util.Vector;

import parser.expression.CTerminal;
import semantic.expression.CBaseEntityCondExpr;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CUnitaryEntityCondition;
import semantic.expression.CBaseEntityCondition.EConditionType;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CDataEntity extends CBaseLanguageEntity
{
	public enum CDataEntityType
	{
		VAR, 
		NUMERIC_VAR,
		EXTERNAL_REFERENCE,
		FIELD,
		FIELD_ATTRIBUTE,
		FORM,
		CONSTANT,
		NUMBER,
		STRING,
		CONDITION,
		CONSOLE_KEY,
		IGNORE,
		VIRTUAL_FORM, 
		EXPRESSION,
		ADDRESS,
		UNKNWON
	} 
	
	public abstract CDataEntityType GetDataType();

	/**
	 * @param name
	 * @param cat
	 */
//	public CDataEntity(int nLine)
//	{
//		super(nLine);
//	}

	
	protected CDataEntity(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
	 */

	protected void RegisterMySelfToCatalog()
	{
		programCatalog.RegisterDataEntity(GetName(), this) ;
	}

	public abstract String ExportReference(int nLine) ;
	
	public String export()
	{
		return ExportReference(getLine());
	}
	public int getNbDimOccurs()
	{
		return 0;
	}
	
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		return null ;
	} ;
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l) 
	{
		return null ;
	}
	
//	abstract public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory);
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		if(getClass().getName().endsWith("CJavaUnknownReference"))
			Transcoder.logError(nLine, "ERROR : special condition needed for value '"+value + "; Undefined variable: "+GetName());
		else
			Transcoder.logError(nLine, "ERROR : special condition needed for value '"+value + "; Undefined variable: "+GetName() + " for class="+getClass().getName());
		return null ;
	}
		
	public CBaseExternalEntity of = null ;
	
	public abstract boolean HasAccessors() ;
	public abstract String ExportWriteAccessorTo(String value) ;
	
	public abstract boolean isValNeeded();
	public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory) 
	{
		Transcoder.logError(getLine(), "Error, substring not implemented for variable: " + GetName()) ;
		return null ;
	};
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory) 
	{
		Transcoder.logError(getLine(), "Error, GetArray not implemented for variable: " + GetName()) ;
		return null ;
	};
	
	public CUnitaryEntityCondition GetAssociatedCondition(CBaseEntityFactory factory)
	{
//		m_logger.error("GetAssociatedCondition not implemented for this tye of data") ;
		return null ;
	}

	public CBaseEntityCondition GetSpecialCondition(int nLine, CDataEntity eData2, EConditionType type, CBaseEntityFactory factory)
	{
		return null;
	}
	
	// algorythmic analysis
	protected Vector<CBaseActionEntity> arrActionsWriting = new Vector<CBaseActionEntity>() ; // when this var is accessed in write mode : MOVE a TO THIS
	protected Vector<CBaseActionEntity> arrActionsReading = new Vector<CBaseActionEntity>() ; // when this var is accessed in read mode : MOVE THIS TO a
	protected Vector<CGenericDataEntityReference> writeReference = new Vector<CGenericDataEntityReference>() ; // when this var is accessed in write mode : MOVE a TO THIS
	protected Vector<CGenericDataEntityReference> readReference = new Vector<CGenericDataEntityReference>() ; // when this var is accessed in read mode : MOVE THIS TO a
	protected Vector<CBaseEntityCondition> arrTestsAsVar = new Vector<CBaseEntityCondition>() ; // when this var is tested : IF THIS = a / IF IS NUMERIC(THIS)
	protected Vector<CBaseEntityCondExpr> accessAsValue = new Vector<CBaseEntityCondExpr>() ; // when the value of this var occures in a test : IF a = THIS
	protected Vector<CEntityFileDescriptor> fileDescriptorDepending = new Vector<CEntityFileDescriptor>() ; // when this var is used in a file descriptor : DEPENDING ON THIS
	
	public void RegisterReadReference(CGenericDataEntityReference ent)
	{
		readReference.add(ent) ;
	}
	public void RegisterWriteReference(CGenericDataEntityReference ent)
	{
		writeReference.add(ent) ;
	}
	public void RegisterWritingAction(CBaseActionEntity act)
	{
		arrActionsWriting.add(act) ;
	}
	public void UnRegisterWritingAction(int i)
	{
		arrActionsWriting.remove(i) ;
	}
	public void UnRegisterWritingAction(CBaseActionEntity e)
	{
		arrActionsWriting.remove(e) ;
	}
	public void RegisterFileDescriptorDepending(CEntityFileDescriptor fileDescriptor)
	{
		fileDescriptorDepending.add(fileDescriptor) ;
	}
	public int GetNbWriteReferences()
	{
		return writeReference.size();
	}
	public int GetNbReadReferences()
	{
		return readReference.size();
	}
	public int GetNbWrittingActions()
	{
		return arrActionsWriting.size();
	}
	public CBaseActionEntity GetActionWriting(int i)
	{
		if (i<arrActionsWriting.size())
		{
			return arrActionsWriting.get(i);
		}
		else
		{
			return null ;
		}
	}
	public CGenericDataEntityReference GetWriteReference(int i)
	{
		if (i< writeReference.size())
		{
			return writeReference.get(i);
		}
		else
		{
			return null ;
		}
	}
	public CGenericDataEntityReference GetReadReference(int i)
	{
		if (i< readReference.size())
		{
			return readReference.get(i);
		}
		else
		{
			return null ;
		}
	}
	public void RegisterReadingAction(CBaseActionEntity act)
	{
		arrActionsReading.add(act) ;
	}
	public void UnRegisterReadingAction(int i)
	{
		arrActionsReading.remove(i) ;
	}
	public void UnRegisterReadingAction(CBaseActionEntity e)
	{
		arrActionsReading.remove(e) ;
	}
	public int GetNbReadingActions()
	{
		return arrActionsReading.size();
	}
	public CBaseActionEntity GetActionReading(int i)
	{
		if (i<arrActionsReading.size())
		{
			return arrActionsReading.get(i);
		}
		else
		{
			return null ;
		}
	}
	public void RegisterVarTesting(CBaseEntityCondition cond)
	{
		arrTestsAsVar.add(cond) ;
	}
	public void UnRegisterVarTesting(int i)
	{
		arrTestsAsVar.remove(i) ;
	}
	public int GetNbVarTesting()
	{
		return arrTestsAsVar.size();
	}
	public CBaseEntityCondition GetVarTesting(int i)
	{
		if (i<arrTestsAsVar.size())
		{
			return arrTestsAsVar.get(i);
		}
		else
		{
			return null ;
		}
	}
	public void RegisterValueAccess(CBaseEntityCondExpr cond)
	{
		accessAsValue.add(cond) ;
	}
	public void UnRegisterValueAccess(int i)
	{
		accessAsValue.remove(i) ;
	}
	public int GetNbValueAccess()
	{
		return accessAsValue.size();
	}
	public CBaseEntityCondExpr GetValueAccess(int i)
	{
		if (i< accessAsValue.size())
		{
			return accessAsValue.get(i);
		}
		else
		{
			return null ;
		}
	}

	public abstract String GetConstantValue() ;
	public boolean ignore()
	{
		boolean ignore = arrActionsReading.size()== 0 ;
		ignore &= arrActionsWriting.size() == 0 ;
		ignore &= readReference.size() == 0 ;
		ignore &= writeReference.size() == 0 ;
		ignore &= accessAsValue.size() == 0 ;
		ignore &= arrTestsAsVar.size() == 0 ;
		ignore &= fileDescriptorDepending.size() == 0 ;
		ignore &= lstChildren.size()== 0 ;
		if (ignore)
		{
			return true ;
		}
		return isignore;
	}
	public void ReplaceBy(CDataEntity var)
	{
		for (int j = 0; j<arrActionsReading.size();)
		{
			CBaseActionEntity act = arrActionsReading.get(j);
			if (!act.ReplaceVariable(this, var))
				j++ ;
		}
		for (int j = 0; j<arrActionsWriting.size(); )
		{
			CBaseActionEntity act = arrActionsWriting.get(j);
			if (!act.ReplaceVariable(this, var))
				j++ ;
		}
		for (int j = 0; j< accessAsValue.size();)
		{
			CBaseEntityCondExpr act = accessAsValue.get(j);
			if (!act.ReplaceVariable(this, var))
				j++ ;
		}
		for (int j = 0; j<arrTestsAsVar.size(); )
		{
			CBaseEntityCondition act = arrTestsAsVar.get(j);
			if (!act.ReplaceVariable(this, var))
				j++ ;
		}
		for (int j = 0; j< readReference.size(); )
		{
			CGenericDataEntityReference act = readReference.get(j);
			if (!act.ReplaceVariable(this, var, true))
				j++ ;
		}
		for (int j = 0; j< writeReference.size();)
		{
			CGenericDataEntityReference act = writeReference.get(j);
			if (!act.ReplaceVariable(this, var, false))
				j++ ;
		}
	}
	public void Clear()
	{
		super.Clear();
		arrActionsReading.clear() ;
		arrActionsWriting.clear() ;
		readReference.clear() ;
		accessAsValue.clear() ;
		arrTestsAsVar.clear() ;
		writeReference.clear() ;
		fileDescriptorDepending.clear() ;
		if (of != null)
		{
			of = null ;
		}
	}
	
	public void ResetReferenceCount()
	{
		arrActionsReading.clear() ;
		arrActionsWriting.clear() ;
		readReference.clear() ;
		accessAsValue.clear() ;
		arrTestsAsVar.clear() ;
		writeReference.clear() ;
		fileDescriptorDepending.clear() ;
	}
	public int getActualSubLevel()
	{
		return 0 ;
	}

	public void UnRegisterReadReference(CBaseDataReference reference)
	{
		readReference.remove(reference) ;
	}

	public void UnRegisterWriteReference(CBaseDataReference reference)
	{
		writeReference.remove(reference) ;
	}

	public void UnRegisterVarTesting(CBaseEntityCondition cond)
	{
		arrTestsAsVar.remove(cond) ;
	}

	public void UnRegisterValueAccess(CBaseEntityCondExpr attribute)
	{
		accessAsValue.remove(attribute) ;
	}

	// ==================== ST4 Template Accessors ====================

	public CBaseExternalEntity getOfQualifier()
	{
		return of;
	}

}
