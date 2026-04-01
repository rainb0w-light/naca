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
import lexer.Cobol.CCobolConstantList;
import parser.Cobol.elements.CWorkingEntry.CWorkingSignType;
import parser.expression.CTerminal;
import semantic.Verbs.*;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondIsConstant;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityAttribute extends CGenericDataEntityReference implements ITypableEntity
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
	public CEntityAttribute(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat, out);
	}
	public void SetComp(String s)
	{
		comp = s ;
	}
	public void SetTypeString(int length) 
	{
		type = "picX" ;
		length = length ;
	};
	public void SetTypeNum(int length, int dec)
	{
		type = "pic9" ;
		length = length ;
		decimals = dec ;
	};
	public void SetTypeSigned(int length, int dec)
	{
		type = "picS9" ;
		length = length ;
		decimals = dec ;
	};
	public void SetInitialValueSpaces()
	{
		isinitialValueIsSpaces = true ;
		isinitialValueIsZeros = false ;
		isinitialValueIsLowValue = false ;
		isinitialValueIsHighValue = false ;
		value = null ;
	}
	public void SetInitialValueZeros()
	{
		isinitialValueIsSpaces = false ;
		isinitialValueIsZeros = true ;
		isinitialValueIsLowValue = false ;
		isinitialValueIsHighValue = false ;
		value = null ;
	}
	public void SetInitialLowValue()
	{
		value = null ;
		isinitialValueIsSpaces = false ;
		isinitialValueIsZeros = false ;
		isinitialValueIsLowValue = true ;
		isinitialValueIsHighValue = false ;
	}
	public void SetInitialHighValue()
	{
		value = null ;
		isinitialValueIsSpaces = false ;
		isinitialValueIsZeros = false ;
		isinitialValueIsLowValue = false ;
		isinitialValueIsHighValue = true ;
	}
	public void SetInitialValueAll(CDataEntity s)
	{
		value = s ;
		isfillWithValue = true ;
		isinitialValueIsSpaces = false ;
		isinitialValueIsZeros = false ;
		isinitialValueIsLowValue = false ;
		isinitialValueIsHighValue = false ;
	}
	public void SetInitialValue(CDataEntity s)
	{
		value = s ;
		isinitialValueIsSpaces = false ;
		isinitialValueIsZeros = false ;
		isinitialValueIsLowValue = false ;
		isinitialValueIsHighValue = false ;
	}
	public void SetTypeEdited(String f)
	{
		type = "pic" ;
		length = 0;
		decimals = 0;
		format =f ;
	}

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
	public void SetSync(boolean b)
	{
		issync = b ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String value = term.GetValue() ;
		CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
		if (value.equals(CCobolConstantList.ZERO.name) || value.equals(CCobolConstantList.ZEROS.name) || value.equals(CCobolConstantList.ZEROES.name))
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
			if (typeCopy.equals(""))
				typeCopy = "GROUP" ;
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
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null;
	}

	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
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
//		else if (type.equals("picX"))
//		{
//			try
//			{
//				int n = Integer.parseInt(value) ;
//				if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
//				{
//					CEntityCondEquals cond = factory.NewEntityCondEquals() ;
//					cond.SetDifferentCondition(factory.NewEntityExprTerminal(this), factory.NewEntityExprTerminal(factory.NewEntityString(value))) ;
//					m_logger.info("line "+getLine()+" : numeric value converted to string to compare with PICX var : " + value) ;
//					return cond ;
//				}
//				else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
//				{
//					CEntityCondEquals cond = factory.NewEntityCondEquals() ;
//					cond.SetEqualCondition(factory.NewEntityExprTerminal(this), factory.NewEntityExprTerminal(factory.NewEntityString(value))) ;
//					m_logger.info("line "+getLine()+" : numeric value converted to string to compare with PICX var : " + value) ;
//					return cond ;
//				}
//				else
//				{
//					m_logger.info("line "+getLine()+" : numeric value to compare with EDIT var not managed : " + value) ;
//					return null ;
//				}
//			}
//			catch (NumberFormatException e)
//			{
//				return null ;
//			}
//		}
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
	public int GetInternalLevel()
	{
		return 1 ;
	} 
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
	public void Clear()
	{
		super.Clear();
		value = null ;
	}
	public void SetJustifiedRight(boolean bJustifiedRight)
	{
		bJustifiedRight = bJustifiedRight ;
	}
	protected boolean isjustifiedRight = false ;
	
	public void SetBlankWhenZero(boolean blankWhenZero)
	{
		isblankWhenZero = blankWhenZero ;
	}
	protected boolean isblankWhenZero = false ;
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

}
