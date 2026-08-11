/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseActionEntity;
import semantic.CBaseDataReference;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondExpr;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: the CICS AID-key pseudo-variable registered from the
 * {@code keyPressed} transcoder rules ({@code keyName} -> a {@code nacaLib.misc.KeyPressed}
 * constant such as {@code PF1}/{@code ENTER}, {@code CICSAlias} -> the entity name).
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaKeyPressed}
 * was retired onto the recursive ST4 assembly contract. The backend's only live output
 * protocol was {@code ExportReference(nLine) == "KeyPressed." + csPublicName} — a static
 * constant reference into {@code nacaLib.misc.KeyPressed} (e.g. {@code KeyPressed.PF1}),
 * not a runtime method call. That reference now renders through the BMS forms-island
 * binding {@code semantic.forms.CEntityKeyPressed -> recursiveKeyPressedEntity}; the
 * template reads only {@code entity.publicName} and prepends the target-specific
 * {@code KeyPressed.} qualifier. {@code csPublicName} is precomputed here at construction
 * time (from the rule's {@code keyName}); no output protocol lives in this tree.
 *
 * <p>The {@code HasAccessors() == false} / {@code isValNeeded() == false} protocols the
 * retired backend forced are preserved exactly: a console key is a pseudo-constant
 * reference, never a declared {@code val} and never an accessor-bearing variable, so its
 * (unused) write-accessor protocol is never reached. The {@code addImportDeclaration("KEYPRESSED")}
 * side effects stay: they surface as {@code import nacaLib.misc.KeyPressed;} through
 * {@code CEntityClass.isKeyPressed()} in the program root template.
 *
 * @author U930CV
 */
public class CEntityKeyPressed extends CDataEntity
{
	public void RegisterReadingAction(CBaseActionEntity act)
	{
		super.RegisterReadingAction(act);
		programCatalog.addImportDeclaration("KEYPRESSED") ;
	}
	public void RegisterReadReference(CBaseDataReference ent)
	{
		programCatalog.addImportDeclaration("KEYPRESSED") ;
		super.RegisterReadReference(ent);
	}
	public void RegisterValueAccess(CBaseEntityCondExpr cond)
	{
		programCatalog.addImportDeclaration("KEYPRESSED") ;
		super.RegisterValueAccess(cond);
	}
	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param publicName the {@code nacaLib.misc.KeyPressed} constant name (rule {@code keyName})
	 */
	public CEntityKeyPressed(int l, String name, CObjectCatalog cat, String publicName)
	{
		super(l, name, cat);
		csPublicName = publicName ;
	}
	protected String csPublicName = "" ;

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.CONSOLE_KEY ;
	}

	public boolean HasAccessors()
	{
		// A console key is a pseudo-constant reference, never an accessor-bearing variable.
		return false;
	}

	public boolean isValNeeded()
	{
		// A console key is never declared as a val.
		return false;
	}

	public boolean ignore()
	{
		return false ;
	}
	public String GetConstantValue()
	{
		return "" ;
	}

	/**
	 * Pure read-only getter consumed by the recursive ST4 assembly contract
	 * (semantic.forms.CEntityKeyPressed -> recursiveKeyPressedEntity), preserved from the
	 * retired direct backend generate.java.forms.CJavaKeyPressed whose ExportReference
	 * returned "KeyPressed." + csPublicName. Exposes only the precomputed
	 * {@code nacaLib.misc.KeyPressed} constant name; the template prepends the
	 * target-specific {@code KeyPressed.} qualifier. A pure field read — no
	 * FormatIdentifier, no data-reference resolution, no lowering.
	 */
	public String getPublicName()
	{
		return csPublicName ;
	}
}
