/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityNumber extends CDataEntity
{

    protected String csValue = "" ;
    private boolean preserveSourceLexeme = false ;
    private boolean valueNeeded = true ;

    /** Creates a new centity number instance. */
    public CEntityNumber(CObjectCatalog cat, String number)
    {
        super(0, "", cat);
        csValue = number;
    }
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.NUMBER;
    }
    public String getLiteralValue()
    {
        return csValue;
    }
    public boolean isPreserveSourceLexeme()
    {
        return preserveSourceLexeme ;
    }
    /** Executes the preserve source lexeme operation. */
    public void preserveSourceLexeme()
    {
        preserveSourceLexeme = true ;
    }
    public boolean isHexLiteral()
    {
        return preserveSourceLexeme && csValue.startsWith("0x") ;
    }
    public String getHexDigits()
    {
        return isHexLiteral() ? csValue.substring(2) : csValue ;
    }
    public void setValueNeeded(boolean valueNeeded)
    {
        this.valueNeeded = valueNeeded ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return csValue;
    }
    public boolean isDecimalLiteral()
    {
        return csValue.indexOf('.') >= 0;
    }
    /** Returns the normalized literal value. */
    public String getNormalizedLiteralValue()
    {
        if (isDecimalLiteral()) {
            return csValue;
        }
        try
        {
            return String.valueOf(Integer.parseInt(csValue));
        }
        catch (NumberFormatException integerError)
        {
            try
            {
                return String.valueOf(Long.parseLong(csValue));
            }
            catch (NumberFormatException longError)
            {
                return csValue;
            }
        }
    }
    /** Returns whether long literal. */
    public boolean isLongLiteral()
    {
        if (isDecimalLiteral()) {
            return false;
        }
        try
        {
            Integer.parseInt(csValue);
            return false;
        }
        catch (NumberFormatException integerError)
        {
            try
            {
                Long.parseLong(csValue);
                return true;
            }
            catch (NumberFormatException longError)
            {
                return false;
            }
        }
    }
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    public boolean isValNeeded()
    {
        return valueNeeded;
    }
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        return null ;
    }
}
