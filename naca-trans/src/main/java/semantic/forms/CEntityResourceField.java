/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import generate.CBaseLanguageExporter;

import java.util.Vector;

import lexer.Cobol.CCobolConstantList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CExpression;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseResourceEntity;
import semantic.CEntityArrayReference;
import semantic.CSubStringAttributReference;
import semantic.ITypableEntity;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntitySetConstant;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondIsConstant;
import semantic.expression.CBaseEntityCondition.EConditionType;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityResourceField extends CBaseResourceEntity  implements ITypableEntity
{
	/* (non-Javadoc)
	 * @see semantic.ITypableEntity#SetTypeEdited(java.lang.String)
	 */
	protected String format = "" ;
	protected String type = "" ;
	public void SetTypeEdited(String format)
	{
		type = "pic" ;
		nLength = 0;
		nDecimals = 0;
		format = format ;
	}
	/* (non-Javadoc)
	 * @see semantic.ITypableEntity#SetTypeNum(int, int)
	 */
	public void SetTypeNum(int length, int decimal)
	{
		type = "pic9" ;
		nLength = length ;
		nDecimals = decimal ;
	}
	/* (non-Javadoc)
	 * @see semantic.ITypableEntity#SetTypeSigned(int, int)
	 */
	public void SetTypeSigned(int length, int decimal)
	{
		type = "picS9" ;
		nLength = length ;
		nDecimals = decimal ;
	}
	/* (non-Javadoc)
	 * @see semantic.ITypableEntity#SetTypeString(int)
	 */
	public void SetTypeString(int length)
	{
		type = "" ;
		nLength = length ;
	}
	protected enum FieldMode
	{
		NORMAL, CHECKBOX,TITLE, ACTIVE_CHOICE, LINKED_ACTIVE_CHOICE, SWITCH, HIDDEN; 
	}
	protected FieldMode mode = FieldMode.NORMAL ;
	/**
	 * @param name
	 * @param cat
	 * @param exp
	 */
	public CEntityResourceField(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp)
	{
		super(l, name, cat, lexp);
	}

//	protected String GetDefaultName()
//	{
//		return "" ;
//	}

	public abstract boolean IsEntryField();
	
	public void InitDependences(CBaseEntityFactory factory)
	{
		String name = GetName() ;
		if (!name.equals(""))
		{
			CBaseEntityFieldAttribute length = factory.NewEntityFieldLengh(getLine(), name+"L", this);
			CBaseEntityFieldAttribute color = factory.NewEntityFieldColor(getLine(), name+"C", this); 
			CBaseEntityFieldAttribute highlight = factory.NewEntityFieldHighlight(getLine(), name+"H", this);
			CBaseEntityFieldAttribute flag = factory.NewEntityFieldFlag(getLine(), name+"P", this);
			CBaseEntityFieldAttribute attributeF = factory.NewEntityFieldAttribute(getLine(), name+"F", this);
			CBaseEntityFieldAttribute attribute = factory.NewEntityFieldAttribute(getLine(), name+"A", this);
//			factory.programCatalog.RegisterDataEntity(name, this);
			factory.programCatalog.RegisterDataEntity(name+"I", this);
			factory.programCatalog.RegisterDataEntity(name+"O", this);
//			CBaseEntityFieldAttribute dataI = factory.NewEntityFieldData(getLine(), name+"I", this);
//			CBaseEntityFieldAttribute dataO = factory.NewEntityFieldData(getLine(), name+"O", this);
			//m_Validation = factory.NewEntityFieldValidation(getLine(), name+"V", CBaseEntityFieldAttribute.CEntityFieldAttributeType.VALIDATION, this);
		}
//		ListIterator iter = lstChildren.listIterator() ;
//		try
//		{
//			CEntityResourceField field = (CEntityResourceField)iter.next() ;
//			while (field != null)
//			{
//				field.InitDependences(factory) ;
//				field = (CEntityResourceField)iter.next() ;
//			}
//		}
//		catch (NoSuchElementException e)
//		{
//		}
	} 
//	CBaseEntityFieldAttribute length = null ;
//	CBaseEntityFieldAttribute color = null ;
//	CBaseEntityFieldAttribute dataI = null ;
//	CBaseEntityFieldAttribute dataO = null ;
//	CBaseEntityFieldAttribute m_Highlight = null ;
//	CBaseEntityFieldAttribute m_Protected = null ;
//	CBaseEntityFieldAttribute m_Flag = null ;
//	CBaseEntityFieldAttribute m_Attribute = null ;
	//CBaseEntityFieldAttribute m_Validation = null ;
	/* (non-Javadoc)
	 * @see semantic.CBaseEntity#RegisterMySelfToCatalog()
	 */
//	protected void RegisterMySelfToCatalog()
//	{
////		programCatalog.RegisterDataEntity(GetName()+"I", this) ;
////		programCatalog.RegisterDataEntity(GetName()+"O", this);
//	}

	public CResourceStrings resourceStrings = null ;
	public int nOccurs = 0 ;
	public int nPosCol = 0 ;
	public int nPosLine = 0 ; 
	public int nLength = 0 ;
	public int nDecimals = 0 ;
	public String csInitialValue = "" ;
	protected String csHighLight = "" ;
	public void Clear()
	{
		super.Clear();
		resourceStrings = null ;
	}

	public void SetHighLight(String cs)
	{
		csHighLight = cs ;
	}
	protected String csColor = "" ;
	public void SetColor(String cs)
	{
		csColor = cs ;
	}
//	protected StringVector arrJustify = new StringVector() ;
//	public void AddJustify(String cs)
//	{
//		arrJustify.addElement(cs);
//	} 
//	protected StringVector arrAttrib = new StringVector() ;
//	public void AddAttrib(String cs)
//	{
//		arrAttrib.addElement(cs);
//	}
	protected String csFillValue = "" ;
	public void SetFillValue(String cs)
	{
		csFillValue = cs ;
	}
	protected String csProtection = "" ;
	public void SetProtection(String cs)
	{
		csProtection = cs ;
	}
	protected String csBrightness = "" ;
	public void SetBrightness(String cs)
	{
		csBrightness = cs ;
	}
	protected boolean bModified = false ;
	public void SetModified()
	{
		bModified = true ;
	}
	protected boolean bCursor = false ;
	public void SetCursor()
	{
		bCursor = true ;
	}
	
	

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
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
		else if (term.IsNumber())
		{
			CEntityAssign asgn = factory.NewEntityAssign(l) ;
			asgn.SetValue(factory.NewEntityString(value)) ;
			asgn.AddRefTo(this) ;
			Transcoder.logDebug(l, "Number converted to string to move into EDIT var : "+value) ;
			RegisterWritingAction(asgn) ;
			return asgn ;
		}
		else
		{
			return null ;
		}
		RegisterWritingAction(eAssign) ;
		return eAssign ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
	 */
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null;
	}
	
	public int GetByteLength ()
	{
		return 7 + nLength ;
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
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		if (type == CBaseEntityCondition.EConditionType.IS_FIELD_ATTRIBUTE)
		{
			return CEntityFieldAttribute.GetSpecialCondition(nLine, value, this, factory, CBaseEntityCondition.EConditionType.IS_EQUAL) ;
		}
		else if (type == CBaseEntityCondition.EConditionType.IS_FIELD_COLOR)
		{
			return CEntityFieldColor.GetSpecialCondition(nLine, value, this, factory, CBaseEntityCondition.EConditionType.IS_EQUAL) ;
		}
//		else if (type == CBaseEntityCondition.ConditionType.IS_FIELD_HIGHLITING)
//		{
//		}
//		else if (type == CBaseEntityCondition.ConditionType.IS_FIELD_PROTECTED)
//		{
//		}
//		else if (type == CBaseEntityCondition.ConditionType.IS_FIELD_MODIFIED)
//		{
//		}
		else
		{			
			CEntityCondIsConstant eCond = factory.NewEntityCondIsConstant() ;
			if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
			{
				eCond.SetIsZero(this);
			}
			else if (value.equals("SPACE") || value.equals("SPACES"))
			{
				eCond.SetIsSpace(this);
			}
			else if (value.equals(CCobolConstantList.LOW_VALUE.name) || value.equals(CCobolConstantList.LOW_VALUES.name))
			{
				eCond.SetIsLowValue(this);
			}
			else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES"))
			{
				eCond.SetIsHighValue(this);
			}
			else
			{
//				try
//				{
//					int n = Integer.parseInt(value) ;
//					if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
//					{
//						CEntityCondEquals cond = factory.NewEntityCondEquals() ;
//						cond.SetDifferentCondition(factory.NewEntityExprTerminal(this), factory.NewEntityExprTerminal(factory.NewEntityString(value))) ;
//						m_logger.info("line "+getLine()+" : numeric value converted to string to compare with EDIT var : " + value) ;
//						return cond ;
//					}
//					else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
//					{
//						CEntityCondEquals cond = factory.NewEntityCondEquals() ;
//						cond.SetEqualCondition(factory.NewEntityExprTerminal(this), factory.NewEntityExprTerminal(factory.NewEntityString(value))) ;
//						m_logger.info("line "+getLine()+" : numeric value converted to string to compare with EDIT var : " + value) ;
//						return cond ;
//					}
//					else
//					{
//						m_logger.info("line "+getLine()+" : numeric value to compare with EDIT var not managed : " + value) ;
//						return null ;
//					}
//				}
//				catch (NumberFormatException e)
//				{
//					return null ;
//				}
				return null ;
			}
			if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
			{
				eCond.SetOpposite();
				RegisterVarTesting(eCond) ;
				return eCond ;
			}
			else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
			{
				RegisterVarTesting(eCond) ;
				return eCond ;
			}
			else if (type == CBaseEntityCondition.EConditionType.IS_GREATER_THAN && value.startsWith("LOW-VALUE"))
			{
				eCond.SetOpposite() ;
				RegisterVarTesting(eCond) ;
				return eCond ;
			}
			else
			{
				return null ;
			}
		}
	}
	public boolean ignore()
	{
		return false ;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#RegisterMySelfToCatalog()
	 */
	protected void RegisterMySelfToCatalog()
	{
		String name = GetName() ;
//		programCatalog.RegisterDataEntity(name, this) ;
		programCatalog.RegisterDataEntity(name+"I", this) ;
		programCatalog.RegisterDataEntity(name+"O", this) ;
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetSpecialCondition(semantic.CDataEntity, semantic.expression.CBaseEntityCondition.ConditionType, semantic.CBaseEntityFactory)
	 */
	public CBaseEntityCondition GetSpecialCondition(int nLine, CDataEntity eData2, EConditionType type, CBaseEntityFactory factory)
	{
		return null ;
	}
	public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory) 
	{
		CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
		ref.SetReference(this, start, length) ;
		return ref ;
	}

	public abstract Element DoXMLExport(Document doc, CResourceStrings res) ;

	public void SetOf(CEntityResourceFormContainer container)
	{
		of = container ;
		CDataEntity [] arr = new CDataEntity[lstChildren.size()] ;
		lstChildren.toArray(arr) ;
		for (int i=0; i<arr.length; i++)
		{
			CDataEntity e = arr[i] ;
			e.of = container ;
		}
	}

	/**
	 * @param string
	 */
//	public void setDisplayName(String string)
//	{
//		csDisplayName = string ;		
//	}
//	protected String csDisplayName = "" ;
	/**
	 * @param valueOn
	 * @param valueOff
	 */
	public void setCheckBox(String valueOn, String valueOff)
	{
		mode = FieldMode.CHECKBOX ;
		csCheckBoxValueOff = valueOff ;
		csCheckBoxValueOn = valueOn ;
	}
	protected String csCheckBoxValueOn = "" ;
	protected String csCheckBoxValueOff = "" ;

	/**
	 * @param flagMark
	 */
	public void setDevelopable(String flagMark)
	{
		csDevelopableFlagMark = flagMark ;
	}
	protected String csDevelopableFlagMark = "" ;
	
	/**
	 * @param flagMark
	 */
	public void setFormat(String format)
	{
		csFormat = format;
	}
	protected String csFormat = "" ;
	
	/**
	 * @param strings
	 * 
	 */
	public void SetTitle(CResourceStrings strings)
	{
		mode = FieldMode.TITLE ;
		if (strings != null)
		{
			strings.FormatResource(csInitialValue) ;
		}
	}

	/**
	 * @param value
	 * @param target
	 * @param submit
	 */
	public void setActiveChoice(String value, String target, boolean submit)
	{
		mode = FieldMode.ACTIVE_CHOICE ;
		csActiveChoiceTarget = target ;
		csActiveChoiceValue = value ;
		bActiveChoiceSubmit = submit ;
	}
	public void setLinkedActiveChoice(String value, String target, boolean submit)
	{
		mode = FieldMode.LINKED_ACTIVE_CHOICE ;
		csActiveChoiceTarget = target ;
		csActiveChoiceValue = value ;
		bActiveChoiceSubmit = submit ;
	}
	protected String csActiveChoiceValue = "" ;
	protected String csActiveChoiceTarget = "" ;
	protected boolean bActiveChoiceSubmit = true ;
	/**
	 * 
	 */
	public void setReplayMutable()
	{
		bReplayMutable = true ;
	}
	protected boolean bReplayMutable = false ;
	public void AddSwitchCase(String value, String protection, Element tag)
	{
		mode = FieldMode.SWITCH ;
		if (arrSwitchCaseElement == null)
		{
			arrSwitchCaseElement = new Vector<CSwitchCaseElement>() ;
		}
		CSwitchCaseElement el = new CSwitchCaseElement() ;
		el.val = value;
		el.protection = protection;
		el.tag = tag ;
		arrSwitchCaseElement.add(el) ;
	}
	protected Vector<CSwitchCaseElement> arrSwitchCaseElement = null ;
	protected class CSwitchCaseElement
	{
		public String val = "" ;
		public String protection = "" ;
		public Element tag = null ;
	}
	public void Hide()
	{
		mode = FieldMode.HIDDEN ;		
	}
	public void SetRightJustified(boolean justifiedRight)
	{
		bRightJustified = justifiedRight ;
	}
	protected boolean bRightJustified = false ;
	
	public void SetBlankWhenZero(boolean blankWhenZero)
	{
		bBlankWhenZero = blankWhenZero ;
	}
	protected boolean bBlankWhenZero = false ;
	
	public void move(int nc, int nl)
	{
		nPosCol = nc ;
		nPosLine = nl ;		
	}
}
