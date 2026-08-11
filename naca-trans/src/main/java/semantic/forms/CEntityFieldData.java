/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;


import java.util.Vector;

import parser.expression.CTerminal;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CSubStringAttributReference;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntitySetConstant;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondIsConstant;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: the DATA attribute of a screen-map field (the {@code I}/{@code O}
 * input/output data reference a {@code DFHMDF} entry field exposes), lowered from
 * {@code CEntityFieldData.GetArrayReference} / the BMS field-attribute construction path
 * ({@code CBaseEntityFactory.NewEntityFieldData}).
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFieldData} was
 * retired. This is the <b>dead-wiring</b> tier (the same tier as {@code CEntityLabelField} /
 * {@code CEntityResourceFieldArray}): the entity is factory-wired but has no live production
 * caller. {@code NewEntityFieldData} is reached only from
 * {@link #GetArrayReference(Vector, CBaseEntityFactory)} — i.e. building a
 * {@code CEntityFieldData} requires one to already exist — so no parser node bootstraps it and
 * the BMS artifact contract ({@code ONLINM1.bms}) never produces one. Accordingly it emits
 * <b>no Java and no XML of its own</b>: the retired backend's {@code DoExport} was an empty
 * stub, it contributes no {@code DoXMLExport} node, and there is no ST4 template/binding for it
 * (its ledger item carries {@code manifestBinding/template == null}).
 *
 * <p>The retired backend's data-entity protocols are preserved exactly and target-neutrally
 * here: a field-data attribute carries {@code FIELD} data type, has no accessors
 * ({@code HasAccessors() == false}), needs a {@code val} ({@code isValNeeded() == true}), has
 * no reachable write-accessor protocol ({@code ExportWriteAccessorTo -> ""}), and its empty
 * {@code DoExport} stays empty. This tree names no {@code generate.*} class, so the dependency
 * arrow stays generate -&gt; semantic.
 *
 * <p>The retired backend's single generate-layer call — {@code ExportReference(nLine)}
 * returning {@code LegacyDataRenderer.renderReference(reference, getLine())} (the OWNER field's
 * rendered reference) — is preserved by an injected reference renderer installed from the
 * generate-layer factory ({@code generate.java.forms.BmsJavaEntities.fieldData} binds the active
 * output and injects {@code LegacyDataRenderer::renderReference}). The renderer is a neutral
 * {@link BiFunction}; a hand-built entity (no factory) defaults to the owner's raw name (or
 * {@code [UNDEFINED]} when unset), mirroring {@code LegacyDataRenderer.renderReference}'s
 * null handling so it never fails. Note {@code LegacyDataRenderer.renderReference} ignores a
 * semantic-declared {@code ExportReference} (declaring class under {@code semantic.*}) and falls
 * through to the recursive assembler — so, exactly as for {@code CEntityFieldOccurs}, this
 * override only serves direct callers and is itself unreachable while the entity stays dead.
 *
 * @author sly
 */
public class CEntityFieldData extends CBaseEntityFieldAttribute
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param type
	 * @param owner
	 */
	public CEntityFieldData(int l, String name, CObjectCatalog cat, CDataEntity owner)
	{
		super(l, name, cat, CEntityFieldAttributeType.DATA, owner);
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}

	public boolean HasAccessors()
	{
		return false ;
	}

	public boolean isValNeeded()
	{
		return true ;
	}

	public String getReferenceName()
	{
		return getReference() == null ? "[UNDEFINED]" : getReference().GetName() ;
	}

	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
	{
		CDataEntity e = reference.GetArrayReference(v, factory) ;
		return factory.NewEntityFieldData(getLine(), "", e);
	};
	public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
	{
		CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
		ref.SetReference(this, start, length) ;
		return ref ;
	};
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		CEntityCondIsConstant eCond = factory.NewEntityCondIsConstant() ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eCond.SetIsZero(reference);
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eCond.SetIsSpace(reference);
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			eCond.SetIsLowValue(reference);
		}
		else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES"))
		{
			eCond.SetIsHighValue(reference);
		}
		else
		{
			return null ;
		}
		if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
		{
			eCond.SetOpposite();
			return eCond ;
		}
		else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
		{
			return eCond ;
		}
		else
		{
			return null ;
		}
	}
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		CEntityAssign eAssign = factory.NewEntityAssign(l) ;
		eAssign.SetValue(term);
		eAssign.AddRefTo(reference) ;
		reference.RegisterWritingAction(eAssign) ;
		return eAssign ;
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String value = term.GetValue() ;
		CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eAssign.SetToZero(reference) ;
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eAssign.SetToSpace(reference) ;
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			eAssign.SetToLowValue(reference) ;
		}
		else
		{
			return null ;
		}
		reference.RegisterWritingAction(eAssign) ;
		return eAssign ;
	}
	public boolean ignore()
	{
		return false ;
	}
}
