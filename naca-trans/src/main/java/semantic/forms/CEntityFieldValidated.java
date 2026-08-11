/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import java.util.Vector;


import parser.expression.CTerminal;
import semantic.*;
import semantic.expression.*;
import utils.CObjectCatalog;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CEntityFieldValidated extends CBaseEntityFieldAttribute
{
	public CEntityFieldValidated(int l, String name, CObjectCatalog cat, CDataEntity owner)
	{
		super(l, name, cat, CEntityFieldAttributeType.VALIDATION, owner) ;
	}
	/*
	 * Semantic predicates preserved from the retired direct backend
	 * (generate.java.forms.CJavaFieldValidated): pure read-only getters, consumed
	 * by the recursive ST4 assembly contract and the BMS traversal; no output
	 * protocol lives here. The reference read renders through the
	 * fieldValidatedReferenceEntity binding ("<owner field reference>.getValidation()"),
	 * exactly the legacy ExportReference shape.
	 */
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public boolean HasAccessors()
	{
		return true ;
	}
	public boolean isValNeeded()
	{
		return false ;
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
//		String col = term.GetValue() ;
//		CEntitySetColor eSet = factory.NewEntitySetColor(l, refField);
//		return eSet;
		return null ;
	}

	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
	{
		CDataEntity e = reference.GetArrayReference(v, factory) ;
		return factory.NewEntityFieldValidated(getLine(), "", e);
	};
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		Transcoder.logError(getLine(), "Unexpecting request with 'Validated' map field attribute (Maybe incoherence MAP/MAPREDEFINE)") ;
		return null ;
	}
}
