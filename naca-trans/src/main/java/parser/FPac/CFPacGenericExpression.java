/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac;

import java.util.Iterator;
import java.util.Vector;

import jlib.misc.NumberParser;

import lexer.CReservedKeyword;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CDefaultConditionManager;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.CSubStringAttributReference;
import semantic.CDataEntity.CDataEntityType;
import semantic.Verbs.CEntityConvertReference;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondCompare;
import semantic.expression.CEntityCondEquals;
import semantic.expression.CEntityCondIsConstant;
import semantic.expression.CEntityCondIsKindOf;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;
import utils.Transcoder;
import utils.FPacTranscoder.OperandDescription;
import utils.FPacTranscoder.notifs.NotifGetDefaultInputFile;
import utils.FPacTranscoder.notifs.NotifGetDefaultOutputFile;

public class CFPacGenericExpression extends CExpression
{

	private CReservedKeyword keyword;
	private Vector<CExpression> leftTerms = new Vector<CExpression>() ;
	private Vector<CExpression> rightTerms = new Vector<CExpression>() ;

	public CFPacGenericExpression(int line)
	{
		super(line);
	}

	@Override
	public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
	{
		return null ;
	}

	private OperandDescription FindOperand(Vector<CExpression> arrTerms, CBaseEntityFactory factory)
	{
		Iterator<CExpression> iter = arrTerms.iterator() ;
		CExpression exp = iter.next() ;
		OperandDescription desc = new OperandDescription() ;
		if (exp.IsConstant() || exp.IsReference())
		{
			CEntityExprTerminal term = (CEntityExprTerminal)exp.AnalyseExpression(factory) ;
			if (term.GetDataType() == CDataEntityType.NUMBER)
			{
				String val = term.GetConstantValue() ;
				CEntityNumber number = factory.NewEntityNumber(val) ;
				desc.eObject = number ;
				desc.expStart = null ;
				desc.expLength = null ;
				if (val.startsWith("0x"))
				{
					desc.expLength = factory.NewEntityExprTerminal(factory.NewEntityNumber((val.length()-2)/2)) ;
					if (!isPackedHexa(val.substring(2)))
						desc.ishexaNoPacked = true;
				}
				else
				{
					desc.expLength = factory.NewEntityExprTerminal(factory.NewEntityNumber(8)) ;
				}
				return desc ;
			}
			else if (term.GetDataType() == CDataEntityType.STRING)
			{
				String val = term.GetConstantValue() ;
				CEntityString string = factory.NewEntityString(val) ;
				desc.eObject = string ;
				desc.expStart = null ;
				desc.expLength = factory.NewEntityExprTerminal(factory.NewEntityNumber(val.length()))  ;
				return desc ;
			}
			else if (term.GetDataType() == CDataEntityType.ADDRESS)
			{
				String val = term.GetConstantValue() ;
				int add = NumberParser.getAsInt(val) ;
				
				if (add < 5000)
				{ //file buffer 
					CDataEntity buffer = getDefaultInputFileBuffer(factory.programCatalog) ;
					desc.eObject = buffer ;
					desc.expStart = term ;
				}
				else
				{ // working
					CDataEntity working = factory.programCatalog.GetDataEntity("WORKING", "") ;
					desc.eObject = working ;
					desc.expStart = term ;
				}
			}
			else if (term.GetDataType() == CDataEntityType.NUMERIC_VAR)
			{
				CDataEntity var = term.GetSingleOperator() ;
				desc.eObject = var ;
				desc.expStart = null ;
			}
			else if (term.GetDataType() == CDataEntityType.VAR)
			{
				CDataEntity var = term.GetSingleOperator() ;
				if (iter.hasNext())
				{
					CExpression expstart = iter.next()  ;
					CBaseEntityExpression termstart = expstart.AnalyseExpression(factory) ;
					if (termstart.GetDataType() == CDataEntityType.ADDRESS)
					{
						desc.eObject = var ;
						desc.expStart = termstart ;
					}
				}
				else
				{
					desc.eObject = var ;
					desc.expStart = null ;
				}
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting expression : " + exp.toString()) ;
				return null ;
			}
		}
		else
		{
			CExpression op = exp.GetFirstCalculOperand() ;
			CBaseEntityExpression term = exp.AnalyseExpression(factory) ;
			if (op.IsConstant())
			{
				String val = op.GetConstantValue() ;
				int add = NumberParser.getAsInt(val) ;
				if (add < 5000)
				{ //file buffer
					CDataEntity buffer = getDefaultInputFileBuffer(factory.programCatalog) ;
					desc.eObject = buffer ;
					desc.expStart = term ;
					/*CDataEntity buffer = getDefaultOutputFileBuffer(factory.programCatalog) ;
					desc.eObject = buffer ;
					desc.expStart = term ;*/
				}
				else
				{ // working
					CDataEntity working = factory.programCatalog.GetDataEntity("WORKING", "") ;
					desc.eObject = working ;
					desc.expStart = term ;
				}
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting expression : " + exp.toString()) ;
				return null ;
			}
		}
		
		if (iter.hasNext())
		{
			exp = iter.next() ;
			CEntityExprTerminal term = (CEntityExprTerminal)exp.AnalyseExpression(factory) ;
			if (term.GetDataType() == CDataEntityType.ADDRESS)
			{
				desc.expLength = term ;
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting expression :  " + exp.toString()) ;
				return null ;
			}
		}
		return desc;
	}
	
	private boolean isPackedHexa(String cs)	// Autodermine if the cs value is a packed one or a string described in hexadecimal codes
	{
		for(int n=0; n<cs.length()-1; n++)
		{
			char c = cs.charAt(n);
			if(c < '0' || c > '9')
				return false;
		}
		char c = cs.charAt(cs.length()-1);
		if(c == 'D' || c == 'C')
			return true;
		return false;
	}
	
	private void manageDataTypeDependingOnOperationType(OperandDescription desc1, OperandDescription desc2, CBaseEntityFactory factory) {
		CDataEntity firstObject = desc1.eObject ;
		if (desc1.expStart != null)
		{
			CEntityConvertReference conv = factory.NewEntityConvert(getLine());
			if (keyword == CFPacKeywordList.LE
					|| keyword == CFPacKeywordList.LT
					|| keyword == CFPacKeywordList.GE
					|| keyword == CFPacKeywordList.GT)
			{
				if (desc2.eObject.GetDataType() == CDataEntityType.STRING)
				{
					conv.convertToAlphaNum(desc1.eObject) ;
				}
				else if (desc2.eObject.GetDataType() == CDataEntityType.VAR)
				{
					conv.convertToAlphaNum(desc1.eObject) ;
				}
				else
				{
					if (desc2.ishexaNoPacked)
						conv.convertToAlphaNum(desc1.eObject) ;
					else
						conv.convertToPacked(desc1.eObject) ;
				}
			}
			else if (keyword == CFPacKeywordList.EQ || keyword == CFPacKeywordList.NE)
			{
				if (desc2.eObject.GetDataType() == CDataEntityType.NUMBER)
				{
					if (desc2.ishexaNoPacked)
						conv.convertToAlphaNum(desc1.eObject) ;
					else
						conv.convertToPacked(desc1.eObject) ;
				}
				else
				{
					conv.convertToAlphaNum(desc1.eObject) ;
				}
			}
			else
			{
				conv.convertToAlphaNum(desc1.eObject) ;
			}
			desc1.eObject = conv ;
		}
		if (desc2.expStart != null)
		{
			CEntityConvertReference conv = factory.NewEntityConvert(getLine());
			if (keyword == CFPacKeywordList.LE
					|| keyword == CFPacKeywordList.LT
					|| keyword == CFPacKeywordList.GE
					|| keyword == CFPacKeywordList.GT)
			{
				if (firstObject.GetDataType() == CDataEntityType.STRING)
				{
					conv.convertToAlphaNum(desc2.eObject) ;
				}
				else if (firstObject.GetDataType() == CDataEntityType.VAR)
				{
					conv.convertToAlphaNum(desc2.eObject) ;
				}
				else
				{
					conv.convertToPacked(desc2.eObject) ;
				}
			}
			else if (keyword == CFPacKeywordList.EQ || keyword == CFPacKeywordList.NE)
			{
				if (firstObject.GetDataType() == CDataEntityType.NUMBER)
				{
					conv.convertToPacked(desc2.eObject) ;
				}
				else
				{
					conv.convertToAlphaNum(desc2.eObject) ;
				}
			}
			else
			{
				conv.convertToAlphaNum(desc2.eObject) ;
			}
			desc2.eObject = conv ;
		}
	}

	private CDataEntity getDefaultInputFileBuffer(CObjectCatalog catalog)
	{
		NotifGetDefaultInputFile notif = new NotifGetDefaultInputFile() ;
		catalog.SendNotifRequest(notif) ;
		return notif.fileBuffer ;
	}

	private CDataEntity getDefaultOutputFileBuffer(CObjectCatalog catalog)
	{
		NotifGetDefaultOutputFile notif = new NotifGetDefaultOutputFile() ;
		catalog.SendNotifRequest(notif) ;
		return notif.fileBuffer ;
	}

	@Override
	public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager masterCond)
	{
		//  analyse operands
		OperandDescription desc1 = FindOperand(leftTerms, factory) ;
		if (rightTerms == null || rightTerms.isEmpty())
		{
			return AnalyseSingleOperand(desc1, factory) ;
		}
		OperandDescription desc2 = FindOperand(rightTerms, factory) ;
		if (desc1 == null || desc2 == null)
		{
			return null ;
		}

		// manage buffer type
		manageDataTypeDependingOnOperationType(desc1, desc2, factory) ;
		
		// build data entities 
		CDataEntity e1, e2 ;
		if (desc2.expLength != null && desc1.expLength == null && desc1.expStart != null)
		{
			desc1.expLength = desc2.expLength ;
		}
		if (desc1.expLength != null && desc2.expLength == null && desc2.expStart != null)
		{
			desc2.expLength = desc1.expLength ;
		}
		if (desc1.expStart != null)
		{
			CSubStringAttributReference ss = factory.NewEntitySubString(getLine()) ;
			ss.SetReference(desc1.eObject, desc1.expStart, desc1.expLength) ;
			e1 = ss ;
		}
		else
		{
			e1 = desc1.eObject ;
		}
		if (desc2.expStart != null)
		{
			CSubStringAttributReference ss = factory.NewEntitySubString(getLine()) ;
			ss.SetReference(desc2.eObject, desc2.expStart, desc2.expLength) ;
			e2 = ss ;
		}
		else
		{
			e2 = desc2.eObject ;
		}
		CBaseEntityExpression exp1 = factory.NewEntityExprTerminal(e1) ;
		CBaseEntityExpression exp2 = factory.NewEntityExprTerminal(e2) ;
		
		// analyse keywords
		CBaseEntityCondition condition = null ;
		if (keyword == CFPacKeywordList.EQ)
		{
			CEntityCondEquals cond = factory.NewEntityCondEquals() ;
			cond.SetEqualCondition(exp1, exp2) ;
			condition = cond ;
		}
		else if (keyword == CFPacKeywordList.NE)
		{
			CEntityCondEquals cond = factory.NewEntityCondEquals() ;
			cond.SetDifferentCondition(exp1, exp2) ;
			condition = cond ;
		}
		else if (keyword == CFPacKeywordList.LE)
		{
			CEntityCondCompare cond = factory.NewEntityCondCompare() ;
			cond.SetLessOrEqualThan(exp1, exp2) ;
			condition = cond ;
		}
		else if (keyword == CFPacKeywordList.LT)
		{
			CEntityCondCompare cond = factory.NewEntityCondCompare() ;
			cond.SetLessThan(exp1, exp2) ;
			condition = cond ;
		}
		else if (keyword == CFPacKeywordList.GE)
		{
			CEntityCondCompare cond = factory.NewEntityCondCompare() ;
			cond.SetGreaterOrEqualsThan(exp1, exp2) ;
			condition = cond ;
		}
		else if (keyword == CFPacKeywordList.GT)
		{
			CEntityCondCompare cond = factory.NewEntityCondCompare() ;
			cond.SetGreaterThan(exp1, exp2) ;
			condition = cond ;
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting keyword :  " + keyword.toString()) ;
			return null ;
		}
		exp1.RegisterVarTesting(condition) ;
		exp2.RegisterValueAccess(condition) ;
		return condition ;
	}

	/**
	 * @param desc1
	 * @param factory
	 * @return
	 */
	private CBaseEntityCondition AnalyseSingleOperand(OperandDescription desc1, CBaseEntityFactory factory)
	{
		CDataEntity e1 ;
		if (desc1.expStart != null)
		{
			CEntityConvertReference conv = factory.NewEntityConvert(getLine());
			conv.convertToAlphaNum(desc1.eObject) ;
			CSubStringAttributReference ss = factory.NewEntitySubString(getLine()) ;
			ss.SetReference(conv, desc1.expStart, desc1.expLength) ;
			e1 = ss ;
		}
		else
		{
			e1 = desc1.eObject ;
		}
		if (keyword == CFPacKeywordList.NUMERIC)
		{
			if (desc1.expLength == null)
			{
				desc1.expLength = factory.NewEntityExprTerminal(factory.NewEntityNumber(1)) ;
				CEntityConvertReference conv = factory.NewEntityConvert(getLine());
				conv.convertToAlphaNum(desc1.eObject) ;
				CSubStringAttributReference ss = factory.NewEntitySubString(getLine()) ;
				ss.SetReference(conv, desc1.expStart, desc1.expLength) ;
				e1 = ss;
			}
			CEntityCondIsKindOf eCond = factory.NewEntityCondIsKindOf() ;
			eCond.SetIsNumeric(e1);
			e1.RegisterVarTesting(eCond) ;
			return eCond;
		}
		else if (keyword == CFPacKeywordList.SPACE)
		{
			CEntityCondIsConstant eCond = factory.NewEntityCondIsConstant() ;
			eCond.SetIsSpace(e1);
			e1.RegisterVarTesting(eCond) ;
			return eCond;
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting keyword : " + keyword.toString()) ;
			return null ;
		}
	}

	protected boolean CheckMembersBeforeExport()
	{
		return true;
	}
	
	@Override
	public Element DoExport(Document root)
	{
		Element eExp = root.createElement("Expression") ;
		eExp.setAttribute("Type", keyword.name) ;
		Element eLeft = root.createElement("LeftTerms") ;
		eExp.appendChild(eLeft) ;
		for (CExpression exp : leftTerms)
		{
			CheckMembersBeforeExport();
			eLeft.appendChild(exp.DoExport(root)) ;
		}
		Element eRight = root.createElement("RightTerms") ;
		eExp.appendChild(eRight) ;
		for (CExpression exp : rightTerms)
		{
			eRight.appendChild(exp.DoExport(root)) ;
		}
		return eExp ;
	}

	@Override
	public CExpression GetFirstConditionOperand()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CExpression GetSimilarExpression(CExpression operand)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsBinaryCondition()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void AddTerm(CExpression exp)
	{
		if (keyword == null)
		{
			leftTerms.add(exp) ;
		}
		else
		{
			rightTerms.add(exp);
		}
		
	}

	public void SetKeyword(CReservedKeyword keyword)
	{
		keyword = keyword ;
	}

	@Override
	public CExpression GetFirstCalculOperand()
	{
		return null;
	}

}
