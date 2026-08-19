/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import parser.expression.CTerminal;
import semantic.Verbs.CEntitySetConstant;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CUnitaryEntityCondition;
import utils.CObjectCatalog;



/**
 * @author sly
 *
 */
public class CEntityArrayReference extends CBaseDataReference
{

    /**
     * @param l
     * @param name
     * @param cat
     */
    public CEntityArrayReference(int l, CObjectCatalog cat)
    {
        super(l, "", cat);
    }

    /** Sets the reference. */
    public void SetReference(CDataEntity e)
    {
        reference = e ;
    }
    /** Adds the index. */
    public void AddIndex(CBaseEntityExpression e)
    {
        arrIndexes.add(e);
    }
    protected Vector<CBaseEntityExpression> arrIndexes = new Vector<CBaseEntityExpression>() ;
    public List<CBaseEntityExpression> getIndexes()
    {
        return Collections.unmodifiableList(arrIndexes);
    }
//  protected CDataEntity reference = null ;
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        CBaseEntityCondition eCond = reference.GetSpecialCondition(getLine(), value, type, factory);
        if (eCond == null)
        {
            return null ;
        }
        else
        {
            CDataEntity eData = eCond.GetConditionReference() ;
            CEntityArrayReference eArray = factory.NewEntityArrayReference(getLine()) ;
            eArray.arrIndexes = arrIndexes ;
            eArray.reference = eData ;
            eArray.RegisterVarTesting(eCond) ;
            eCond.SetConditonReference(eArray);
            return eCond;
        }
    }
    /** Executes the get associated condition operation. */
    public CUnitaryEntityCondition GetAssociatedCondition(CBaseEntityFactory factory)
    {
        CUnitaryEntityCondition eCond = reference.GetAssociatedCondition(factory);
        if (eCond == null)
        {
            return null ;
        }
        else
        {
            CDataEntity eData = eCond.GetConditionReference() ;
            CEntityArrayReference eArray = factory.NewEntityArrayReference(getLine()) ;
            eArray.arrIndexes = arrIndexes ;
            eArray.reference = eData ;
            eArray.RegisterVarTesting(eCond) ;
            eCond.SetConditonReference(eArray);
            return eCond;
        }
    }

    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal, semantic.CBaseEntityFactory, int)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        String value = term.GetValue() ;
        CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
        if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
        {
            eAssign.SetToZero(this) ;
        }
        else if (value.equals("SPACE") || value.equals("SPACES"))
        {
            eAssign.SetToSpace(this) ;
        }
        else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
        {
            eAssign.SetToLowValue(this) ;
        }
        else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES"))
        {
            eAssign.SetToHighValue(this) ;
        }
        else
        {
            return null ;
        }
        RegisterWritingAction(eAssign) ;
        return eAssign ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#ignore()
     */
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore() ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
    /** Executes the get sub string reference operation. */
    public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
    {
        CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
        ref.SetReference(this, start, length) ;
        return ref ;
    };

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#Clear()
     */
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        arrIndexes.clear() ;
    }

    public int getNbDimOccurs()
    {
        return 0;
    }

    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }

    public boolean isValNeeded()
    {
        return true;
    }

    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.VAR;
    }
}
