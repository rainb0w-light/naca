/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Vector;
import parser.expression.CTerminal;
import semantic.expression.CBaseEntityCondExpr;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CUnitaryEntityCondition;
import semantic.expression.CBaseEntityCondition.EConditionType;
import utils.CObjectCatalog;
import utils.Transcoder;


/**
 * @author sly
 *
 */
public abstract class CDataEntity extends CBaseLanguageEntity
{
    /** Enumerates supported cdata entity type values. */
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

    /** Executes the get data type operation. */
    public abstract CDataEntityType GetDataType();

    /**
     * @param name
     * @param cat
     */
//  public CDataEntity(int nLine)
//  {
//      super(nLine);
//  }


    protected CDataEntity(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat);
    }

    /* (non-Javadoc)
     * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
     */

    protected void RegisterMySelfToCatalog()
    {
        programCatalog.RegisterDataEntity(GetName(), this) ;
    }

    protected String getSemanticReference()
    {
        return GetName();
    }

    public int getNbDimOccurs()
    {
        return 0;
    }

    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        return null ;
    } ;
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        return null ;
    }

// abstract public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type,
// CBaseEntityFactory factory);
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        if (this instanceof CEntityUnknownReference) {
            Transcoder.logError(nLine, "ERROR : special condition needed for value '" + value + "; Undefined variable: " + GetName());
        } else {
            Transcoder.logError(nLine, "ERROR : special condition needed for value '" + value + "; Undefined variable: " + GetName()
                    + " for class=" + getClass().getName());
        }
        return null ;
    }

    public CBaseExternalEntity of = null ;

    /** Executes the has accessors operation. */
    public abstract boolean HasAccessors() ;
    /** Returns whether val needed. */
    public abstract boolean isValNeeded();
    /** Executes the get sub string reference operation. */
    public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
    {
        Transcoder.logError(getLine(), "Error, substring not implemented for variable: " + GetName()) ;
        return null ;
    };
    /** Executes the get array reference operation. */
    public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
    {
        Transcoder.logError(getLine(), "Error, GetArray not implemented for variable: " + GetName()) ;
        return null ;
    };

    /** Executes the get associated condition operation. */
    public CUnitaryEntityCondition GetAssociatedCondition(CBaseEntityFactory factory)
    {
//      m_logger.error("GetAssociatedCondition not implemented for this tye of data") ;
        return null ;
    }

    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(int nLine, CDataEntity eData2, EConditionType type, CBaseEntityFactory factory)
    {
        return null;
    }

    // algorythmic analysis
    // when this var is accessed in write mode : MOVE a TO THIS
    protected Vector<CBaseActionEntity> arrActionsWriting = new Vector<CBaseActionEntity>() ;
    // when this var is accessed in read mode : MOVE THIS TO a
    protected Vector<CBaseActionEntity> arrActionsReading = new Vector<CBaseActionEntity>() ;
    // when this var is accessed in write mode : MOVE a TO THIS
    protected Vector<CGenericDataEntityReference> writeReference = new Vector<CGenericDataEntityReference>() ;
    // when this var is accessed in read mode : MOVE THIS TO a
    protected Vector<CGenericDataEntityReference> readReference = new Vector<CGenericDataEntityReference>() ;
    // when this var is tested : IF THIS = a / IF IS NUMERIC(THIS)
    protected Vector<CBaseEntityCondition> arrTestsAsVar = new Vector<CBaseEntityCondition>() ;
    // when the value of this var occures in a test : IF a = THIS
    protected Vector<CBaseEntityCondExpr> accessAsValue = new Vector<CBaseEntityCondExpr>() ;
    // when this var is used in a file descriptor : DEPENDING ON THIS
    protected Vector<CEntityFileDescriptor> fileDescriptorDepending = new Vector<CEntityFileDescriptor>() ;

    /** Executes the register read reference operation. */
    public void RegisterReadReference(CGenericDataEntityReference ent)
    {
        readReference.add(ent) ;
    }
    /** Executes the register write reference operation. */
    public void RegisterWriteReference(CGenericDataEntityReference ent)
    {
        writeReference.add(ent) ;
    }
    /** Executes the register writing action operation. */
    public void RegisterWritingAction(CBaseActionEntity act)
    {
        arrActionsWriting.add(act) ;
    }
    /** Executes the un register writing action operation. */
    public void UnRegisterWritingAction(int i)
    {
        arrActionsWriting.remove(i) ;
    }
    /** Executes the un register writing action operation. */
    public void UnRegisterWritingAction(CBaseActionEntity e)
    {
        arrActionsWriting.remove(e) ;
    }
    /** Executes the register file descriptor depending operation. */
    public void RegisterFileDescriptorDepending(CEntityFileDescriptor fileDescriptor)
    {
        fileDescriptorDepending.add(fileDescriptor) ;
    }
    /** Executes the get nb write references operation. */
    public int GetNbWriteReferences()
    {
        return writeReference.size();
    }
    /** Executes the get nb read references operation. */
    public int GetNbReadReferences()
    {
        return readReference.size();
    }
    /** Executes the get nb writting actions operation. */
    public int GetNbWrittingActions()
    {
        return arrActionsWriting.size();
    }
    /** Executes the get action writing operation. */
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
    /** Executes the get write reference operation. */
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
    /** Executes the get read reference operation. */
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
    /** Executes the register reading action operation. */
    public void RegisterReadingAction(CBaseActionEntity act)
    {
        arrActionsReading.add(act) ;
    }
    /** Executes the un register reading action operation. */
    public void UnRegisterReadingAction(int i)
    {
        arrActionsReading.remove(i) ;
    }
    /** Executes the un register reading action operation. */
    public void UnRegisterReadingAction(CBaseActionEntity e)
    {
        arrActionsReading.remove(e) ;
    }
    /** Executes the get nb reading actions operation. */
    public int GetNbReadingActions()
    {
        return arrActionsReading.size();
    }
    /** Executes the get action reading operation. */
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
    /** Executes the register var testing operation. */
    public void RegisterVarTesting(CBaseEntityCondition cond)
    {
        arrTestsAsVar.add(cond) ;
    }
    /** Executes the un register var testing operation. */
    public void UnRegisterVarTesting(int i)
    {
        arrTestsAsVar.remove(i) ;
    }
    /** Executes the get nb var testing operation. */
    public int GetNbVarTesting()
    {
        return arrTestsAsVar.size();
    }
    /** Executes the get var testing operation. */
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
    /** Executes the register value access operation. */
    public void RegisterValueAccess(CBaseEntityCondExpr cond)
    {
        accessAsValue.add(cond) ;
    }
    /** Executes the un register value access operation. */
    public void UnRegisterValueAccess(int i)
    {
        accessAsValue.remove(i) ;
    }
    /** Executes the get nb value access operation. */
    public int GetNbValueAccess()
    {
        return accessAsValue.size();
    }
    /** Executes the get value access operation. */
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

    /** Executes the get constant value operation. */
    public abstract String GetConstantValue() ;
    /** Executes the ignore operation. */
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
    /** Executes the replace by operation. */
    public void ReplaceBy(CDataEntity var)
    {
        for (int j = 0; j<arrActionsReading.size();)
        {
            CBaseActionEntity act = arrActionsReading.get(j);
            if (!act.ReplaceVariable(this, var)) {
                j++;
            }
        }
        for (int j = 0; j<arrActionsWriting.size(); )
        {
            CBaseActionEntity act = arrActionsWriting.get(j);
            if (!act.ReplaceVariable(this, var)) {
                j++;
            }
        }
        for (int j = 0; j< accessAsValue.size();)
        {
            CBaseEntityCondExpr act = accessAsValue.get(j);
            if (!act.ReplaceVariable(this, var)) {
                j++;
            }
        }
        for (int j = 0; j<arrTestsAsVar.size(); )
        {
            CBaseEntityCondition act = arrTestsAsVar.get(j);
            if (!act.ReplaceVariable(this, var)) {
                j++;
            }
        }
        for (int j = 0; j< readReference.size(); )
        {
            CGenericDataEntityReference act = readReference.get(j);
            if (!act.ReplaceVariable(this, var, true)) {
                j++;
            }
        }
        for (int j = 0; j< writeReference.size();)
        {
            CGenericDataEntityReference act = writeReference.get(j);
            if (!act.ReplaceVariable(this, var, false)) {
                j++;
            }
        }
    }
    /** Executes the clear operation. */
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

    /** Resets the reference count. */
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

    /** Executes the un register read reference operation. */
    public void UnRegisterReadReference(CBaseDataReference reference)
    {
        readReference.remove(reference) ;
    }

    /** Executes the un register write reference operation. */
    public void UnRegisterWriteReference(CBaseDataReference reference)
    {
        writeReference.remove(reference) ;
    }

    /** Executes the un register var testing operation. */
    public void UnRegisterVarTesting(CBaseEntityCondition cond)
    {
        arrTestsAsVar.remove(cond) ;
    }

    /** Executes the un register value access operation. */
    public void UnRegisterValueAccess(CBaseEntityCondExpr attribute)
    {
        accessAsValue.remove(attribute) ;
    }

    // ==================== ST4 Template Accessors ====================

    public CBaseExternalEntity getOfQualifier()
    {
        return of;
    }

    /** Preformatted qualifier value exposed for recursive ST templates. */
    public String getQualifierFormattedName()
    {
        return of == null ? null : of.getFormattedName();
    }

}
