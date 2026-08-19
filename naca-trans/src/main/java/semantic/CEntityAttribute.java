/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import lexer.Cobol.CCobolConstantList;
import parser.Cobol.elements.CWorkingEntry.CWorkingSignType;
import parser.expression.CTerminal;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntitySetConstant;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsConstant;
import utils.CObjectCatalog;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CEntityAttribute extends CGenericDataEntityReference implements ITypableEntity
{

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#FindFirstDataEntityAtLevel(int)
     */
    @Override
    public CDataEntity FindFirstDataEntityAtLevel(int level)
    {
        if (level == 1 || level == 77)
        {
            return this ;
        }
        return null ;
    }
    /**
     * @param name
     * @param cat
     */
    public CEntityAttribute(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat);
        if (name.equals(""))
        {
            // FILLER: assign the default name during semantic construction so
            // no backend mutates the tree during generation (mirrors the
            // existing group-filler behavior, now shared by all attributes).
            isfiller = true;
            String defaultName = GetDefaultName();
            if (!defaultName.equals(""))
            {
                SetName(defaultName);
            }
        }
    }
    protected boolean isfiller = false;
    /** Sets the level. */
    public void SetLevel(String level)
    {
        csLevel = level;
    }
    public String getLevel()
    {
        return csLevel;
    }
    /** Sets the comp. */
    public void SetComp(String s)
    {
        comp = s ;
    }
    /** Sets the type string. */
    public void SetTypeString(int length)
    {
        type = "picX" ;
        this.length = length ;
    };
    /** Sets the type num. */
    public void SetTypeNum(int length, int dec)
    {
        type = "pic9" ;
        this.length = length ;
        decimals = dec ;
    };
    /** Sets the type signed. */
    public void SetTypeSigned(int length, int dec)
    {
        type = "picS9" ;
        this.length = length ;
        decimals = dec ;
    };
    /** Sets the initial value spaces. */
    public void SetInitialValueSpaces()
    {
        isinitialValueIsSpaces = true ;
        isinitialValueIsZeros = false ;
        isinitialValueIsLowValue = false ;
        isinitialValueIsHighValue = false ;
        value = null ;
    }
    /** Sets the initial value zeros. */
    public void SetInitialValueZeros()
    {
        isinitialValueIsSpaces = false ;
        isinitialValueIsZeros = true ;
        isinitialValueIsLowValue = false ;
        isinitialValueIsHighValue = false ;
        value = null ;
    }
    /** Sets the initial low value. */
    public void SetInitialLowValue()
    {
        value = null ;
        isinitialValueIsSpaces = false ;
        isinitialValueIsZeros = false ;
        isinitialValueIsLowValue = true ;
        isinitialValueIsHighValue = false ;
    }
    /** Sets the initial high value. */
    public void SetInitialHighValue()
    {
        value = null ;
        isinitialValueIsSpaces = false ;
        isinitialValueIsZeros = false ;
        isinitialValueIsLowValue = false ;
        isinitialValueIsHighValue = true ;
    }
    /** Sets the initial value all. */
    public void SetInitialValueAll(CDataEntity s)
    {
        value = s ;
        isfillWithValue = true ;
        isinitialValueIsSpaces = false ;
        isinitialValueIsZeros = false ;
        isinitialValueIsLowValue = false ;
        isinitialValueIsHighValue = false ;
    }
    /** Sets the initial value. */
    public void SetInitialValue(CDataEntity s)
    {
        value = s ;
        isinitialValueIsSpaces = false ;
        isinitialValueIsZeros = false ;
        isinitialValueIsLowValue = false ;
        isinitialValueIsHighValue = false ;
    }
    /** Sets the type edited. */
    public void SetTypeEdited(String f)
    {
        type = "pic" ;
        length = 0;
        decimals = 0;
        format =f ;
    }

    /** Executes the get sub string reference operation. */
    public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
    {
        CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
        ref.SetReference(this, start, length) ;
        return ref ;
    };

    protected CDataEntity value = null ;
    protected boolean isinitialValueIsSpaces = false ;
    protected boolean isinitialValueIsZeros = false ;
    protected boolean isinitialValueIsLowValue = false ;
    protected boolean isinitialValueIsHighValue = false ;
    protected String comp = "" ;
    protected String type = "" ;
    protected int length = 0 ;
    protected int decimals = 0 ;
    protected String format = "" ;
    protected boolean issync = false ;
    protected boolean isfillWithValue = false ;
    protected String csLevel = "77";
    /** Sets the sync. */
    public void SetSync(boolean b)
    {
        issync = b ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        String value = term.GetValue() ;
        CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
        if (value.equals(CCobolConstantList.ZERO.name) || value.equals(CCobolConstantList.ZEROS.name)
            || value.equals(CCobolConstantList.ZEROES.name))
        {
            eAssign.SetToZero(this) ;
        }
        else if (value.equals(CCobolConstantList.SPACE.name) || value.equals(CCobolConstantList.SPACES.name))
        {
            eAssign.SetToSpace(this) ;
        }
        else if (value.equals(CCobolConstantList.LOW_VALUE.name) || value.equals(CCobolConstantList.LOW_VALUES.name))
        {
            eAssign.SetToLowValue(this) ;
        }
        else if (value.equals(CCobolConstantList.HIGH_VALUE.name) || value.equals(CCobolConstantList.HIGH_VALUES.name))
        {
            eAssign.SetToHighValue(this) ;
        }
        else if (term.IsNumber() && (type.equals("picX") || type.equals("")))
        {
            String typeCopy = type ;
            if (typeCopy.equals("")) {
                typeCopy = "GROUP";
            }
            CEntityAssign asgn = factory.NewEntityAssign(l) ;
            asgn.SetValue(factory.NewEntityString(value)) ;
            asgn.AddRefTo(this) ;
            Transcoder.logDebug(l, "Number converted to string to move into "+typeCopy+" var ("+GetName()+"): "+value) ;
            RegisterWritingAction(asgn) ;
            return asgn ;
        }
        else
        {
            return null ;
        }
        return eAssign ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        return null;
    }

    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        CEntityCondIsConstant eCond = factory.NewEntityCondIsConstant() ;
        if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
        {
            eCond.SetIsZero(this);
        }
        /*else if (value.equals("SPACES") && type == CBaseEntityCondition.EConditionType.IS_GREATER_THAN)
        {
            CEntityCondCompare comp = factory.NewEntityCondCompare() ;
            comp.SetGreaterThan(factory.NewEntityExprTerminal(this),
                            factory.NewEntityExprTerminal(factory.NewEntityConstant(CEntityConstant.Value.SPACES))) ;
            RegisterVarTesting(comp) ;
            return comp ;
        }*/
        else if (value.equals("SPACE") || value.equals("SPACES"))
        {
            eCond.SetIsSpace(this);
        }
        else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
        {
            eCond.SetIsLowValue(this);
        }
        else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES"))
        {
            eCond.SetIsHighValue(this);
        }
//      else if (type.equals("picX"))
//      {
//          try
//          {
//              int n = Integer.parseInt(value) ;
//              if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
//              {
//                  CEntityCondEquals cond = factory.NewEntityCondEquals() ;
// cond.SetDifferentCondition(factory.NewEntityExprTerminal(this), factory.NewEntityExprTerminal(factory.NewEntityString(value))) ;
// m_logger.info("line "+getLine()+" : numeric value converted to string to compare with PICX var : " + value) ;
//                  return cond ;
//              }
//              else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
//              {
//                  CEntityCondEquals cond = factory.NewEntityCondEquals() ;
// cond.SetEqualCondition(factory.NewEntityExprTerminal(this), factory.NewEntityExprTerminal(factory.NewEntityString(value))) ;
// m_logger.info("line "+getLine()+" : numeric value converted to string to compare with PICX var : " + value) ;
//                  return cond ;
//              }
//              else
//              {
// m_logger.info("line "+getLine()+" : numeric value to compare with EDIT var not managed : " + value) ;
//                  return null ;
//              }
//          }
//          catch (NumberFormatException e)
//          {
//              return null ;
//          }
//      }
        else
        {
            return null ;
        }
        RegisterVarTesting(eCond) ;
        if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
        {
            eCond.SetOpposite() ;
            return eCond ;
        }
        else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
        {
            return eCond ;
        }
        else if (type == CBaseEntityCondition.EConditionType.IS_LESS_THAN && value.startsWith("HIGH-VALUE"))
        {
            eCond.SetOpposite() ;
            return eCond ;
        }
        else if (type == CBaseEntityCondition.EConditionType.IS_GREATER_THAN && value.startsWith("LOW-VALUE"))
        {
            eCond.SetOpposite() ;
            return eCond ;
        }
        else
        {
            return null ;
        }
    }
    /** Executes the get internal level operation. */
    public int GetInternalLevel()
    {
        return jlib.misc.NumberParser.getAsInt(csLevel) ;
    }
    /** Executes the get initial value operation. */
    public String GetInitialValue()
    {
        if (value != null)
        {
            return value.GetConstantValue() ;
        }
        else
        {
            return "" ;
        }
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        if (value == null)
        {
            return "" ;
        }
        else
        {
            return value.GetConstantValue() ;
        }
    }
    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#Clear()
     */
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        value = null ;
    }
    /** Sets the justified right. */
    public void SetJustifiedRight(boolean bJustifiedRight)
    {
        isjustifiedRight = bJustifiedRight ;
    }
    protected boolean isjustifiedRight = false ;

    /** Sets the blank when zero. */
    public void SetBlankWhenZero(boolean blankWhenZero)
    {
        isblankWhenZero = blankWhenZero ;
    }
    protected boolean isblankWhenZero = false ;
    /** Sets the sign separate type. */
    public void SetSignSeparateType(CWorkingSignType signSeparateType)
    {
        issignSeparateType = signSeparateType ;
    }
    protected CWorkingSignType issignSeparateType;


    /**
     * @see semantic.CGenericDataEntityReference#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity, boolean)
     */
    @Override
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var, boolean bRead)
    {
        if (value == field)
        {
            value = var ;
            return false;
        }
        return false;
    }
    /**
     * @return Returns the comp.
     */
    public String getComp()
    {
        return comp;
    }

    public String getType()
    {
        return type;
    }

    public int getLength()
    {
        return length;
    }

    public int getDecimals()
    {
        return decimals;
    }

    public CDataEntity getValue()
    {
        return value;
    }

    public boolean isComp3()
    {
        return "Comp3".equalsIgnoreCase(comp);
    }
    public boolean isComp2()
    {
        return "Comp2".equalsIgnoreCase(comp);
    }

    public boolean isComp()
    {
        return "Comp".equalsIgnoreCase(comp) || "Comp4".equalsIgnoreCase(comp);
    }
    public boolean isComp5()
    {
        return "Comp5".equalsIgnoreCase(comp);
    }
    public boolean isBinaryComp()
    {
        return isComp();
    }

    public boolean isFiller()
    {
        return isfiller;
    }

    public CDataEntity getRedefines()
    {
        return null;
    }

    public CDataEntity getOccurs()
    {
        return null;
    }


    public boolean isInitialValueIsSpaces() {
        return isinitialValueIsSpaces;
    }
    public boolean isInitialValueIsZeros() {
        return isinitialValueIsZeros;
    }
    public boolean isInitialValueIsLowValue() {
        return isinitialValueIsLowValue;
    }
    public boolean isInitialValueIsHighValue() {
        return isinitialValueIsHighValue;
    }
    public boolean isSync() {
        return issync;
    }
    public boolean isFillWithValue() {
        return isfillWithValue;
    }
    @Override
    public boolean isDeclarationRequired() {
        return super.isDeclarationRequired()
            || value != null
            || isinitialValueIsSpaces
            || isinitialValueIsZeros
            || isinitialValueIsLowValue
            || isinitialValueIsHighValue;
    }
    public boolean isJustifiedRight() {
        return isjustifiedRight;
    }
    public boolean isBlankWhenZero() {
        return isblankWhenZero;
    }
    public String getFormat() {
        return format;
    }
    public boolean isEditedPicture() {
        return !format.isEmpty();
    }
    public boolean isPictureSizeSpecified() {
        return length > 0 || decimals > 0;
    }
    public boolean isScaled() {
        return decimals > 0;
    }

    /**
     * A BLANK WHEN ZERO numeric item (PIC 9) is declared as an edited numeric
     * picture. Target-agnostic semantic fact used to pick the declared type.
     */
    public boolean isBlankWhenZeroEditedNumeric() {
        return isblankWhenZero && "pic9".equals(type);
    }

    /**
     * Effective declared type. A BLANK WHEN ZERO PIC 9 item is emitted as an
     * edited numeric picture ("pic") instead of "pic9". Read-only: computes a
     * value without mutating the semantic tree, so generating any backend
     * leaves the tree unchanged and is idempotent.
     */
    public String getDeclaredType() {
        return isBlankWhenZeroEditedNumeric() ? "pic" : type;
    }

    /**
     * Effective picture format. For a BLANK WHEN ZERO PIC 9 item this is the
     * edited numeric picture built from length/decimals (e.g. "999.99");
     * otherwise the original format. Read-only.
     */
    public String getDeclaredFormat() {
        if (!isBlankWhenZeroEditedNumeric())
        {
            return format;
        }
        StringBuilder picture = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            picture.append('9');
        }
        if (decimals > 0)
        {
            picture.append('.');
            for (int i = 0; i < decimals; i++)
            {
                picture.append('9');
            }
        }
        return picture.toString();
    }

    public boolean isDeclaredEditedPicture() {
        return !getDeclaredFormat().isEmpty();
    }

    public boolean isDeclaredPictureSizeSpecified() {
        return getDeclaredLength() > 0 || decimals > 0;
    }

    /**
     * Effective declared length. Overridden by structures for variable-length
     * (OCCURS DEPENDING) tables; read-only, never mutates the tree.
     */
    public int getDeclaredLength() {
        return length;
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
        if (type.equals("picS9") || type.equals("pic9"))
        {
            return CDataEntityType.NUMERIC_VAR;
        }
        return CDataEntityType.VAR;
    }
}
