/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import lexer.Cobol.CCobolConstantList;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;
import semantic.expression.CBaseEntityCondition.EConditionType;


/**
 * BMS map-resource DSL: the CICS get-key-pressed pseudo-variable (the AID-key state of the
 * last map I/O). It is the semantic source of two lowerings, both pure semantic analysis that
 * build OTHER entities:
 * <ul>
 *   <li>a comparison against a console key ({@link #GetSpecialCondition}) lowers to a
 *       {@link CEntityIsKeyPressed} condition ({@code isKeyPressed}/{@code isNotKeyPressed});</li>
 *   <li>an assignment of {@code SPACE} ({@link #GetSpecialAssignment}) lowers to a
 *       {@link CEntityResetKeyPressed} action ({@code resetKeyPressed();}).</li>
 * </ul>
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaGetKeyPressed}
 * was retired onto the recursive ST4 assembly contract. The backend carried two output
 * protocols, both preserved target-neutrally:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == "getKeyPressed()"}, a
 *       no-argument call on the program (protected {@code BaseProgram.getKeyPressed()},
 *       inherited by generated program subclasses). That reference now renders through the BMS
 *       forms-island binding {@code semantic.forms.CEntityGetKeyPressed ->
 *       recursiveGetKeyPressedEntity}; the template emits the bare {@code getKeyPressed()} call.
 *       {@code LegacyDataRenderer.renderReference} finds no {@code generate.*}
 *       {@code ExportReference} on this semantic type and falls through to that binding,
 *       reproducing the backend's reference exactly. The runtime call is contracted as
 *       {@code bms.keyPressed.get} (runtime-operations.yaml) and required by the template
 *       (template-runtime-requirements.yaml).</li>
 *   <li><b>write accessor</b> — {@code ExportWriteAccessorTo(value) == "setKeyPressed("+value+") ;"}.
 *       This protocol had no live consumer: {@code LegacyDataRenderer.renderWriteAccessor} is only
 *       reached from the FPac accessor backend, whose factory throws {@code NacaTransAssertException}
 *       for this BMS-only entity, so no FPac tree ever holds it. With the {@code generate.*}
 *       override gone the reflective lookup finds no method and returns null — the protocol retired
 *       without a replacement (same treatment as {@code bms.keyPressed.reference}).</li>
 * </ul>
 *
 * <p>The reference/write-accessor protocols are dead wiring in production: every real use of the
 * pseudo-variable flows through {@link #GetSpecialCondition}/{@link #GetSpecialAssignment} into the
 * {@code CEntityIsKeyPressed}/{@code CEntityResetKeyPressed} entities above, so the
 * {@code getKeyPressed()} reference is preserved for completeness but has no live caller. This tree
 * names no {@code generate.*} class, so the dependency arrow stays generate -&gt; semantic.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: it forced
 * {@code isValNeeded() == false} (the pseudo-variable is never declared as a {@code val}) and
 * inherited {@code HasAccessors() == true} / {@code ignore() == false} / {@code GetConstantValue() == ""}.
 *
 * @author U930CV
 */
public class CEntityGetKeyPressed extends CDataEntity
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntityGetKeyPressed(String name, CObjectCatalog cat)
	{
		super(0, name, cat);
	}

	public CDataEntityType GetDataType()
	{
		// unused
		return null;
	}
	public boolean HasAccessors()
	{
		return true;
	}
	public boolean isValNeeded()
	{
		// Preserved from the retired backend generate.java.forms.CJavaGetKeyPressed: the
		// get-key-pressed pseudo-variable is never declared as a val.
		return false;
	}
	public CBaseEntityCondition GetSpecialCondition(int nLine, CDataEntity eData2, EConditionType type, CBaseEntityFactory factory)
	{
		if (eData2.GetDataType() == CDataEntityType.CONSOLE_KEY)
		{
			if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
			{
				CEntityIsKeyPressed is = factory.NewEntityIsKeyPressed() ;
				eData2.RegisterValueAccess(is) ;
				is.isNotKeyPressed(eData2) ;
				return is ;
			}
			else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
			{
				CEntityIsKeyPressed is = factory.NewEntityIsKeyPressed() ;
				eData2.RegisterValueAccess(is) ;
				is.isKeyPressed(eData2) ;
				return is ;
			}
			else
			{
				return null ;
			}
		}
		else
		{
			return null ;
		}
	}

	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, EConditionType type, CBaseEntityFactory factory)
	{
		// unused
		return null ;
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String val = term.GetValue() ;
		if (val.equals(CCobolConstantList.SPACE.name))
		{
			CEntityResetKeyPressed e = factory.NewEntityResetKeyPressed(l) ;
			return e ;
		}
		else
		{
			return null ;
		}

	}
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public String GetConstantValue()
	{
		return "" ;
	}
}
