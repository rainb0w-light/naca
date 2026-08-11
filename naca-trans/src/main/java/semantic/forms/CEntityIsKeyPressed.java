/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;

/**
 * BMS map-resource DSL: the CICS AID-key test condition (the comparison of the
 * get-key-pressed pseudo-variable against a console key). It is built by
 * {@link CEntityGetKeyPressed#getSpecialCondition} when an {@code IF KEYPRESSED = <key>}
 * / {@code IF KEYPRESSED <> <key>} lowers from a BMS map-resource program: the positive
 * test populates {@link #isKeyPressed(CDataEntity)}, the negated test
 * {@link #isNotKeyPressed(CDataEntity)}.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaIsKeyPressed}
 * was retired onto the recursive ST4 assembly contract. The backend carried a single output
 * protocol, preserved target-neutrally:
 * <ul>
 *   <li><b>condition</b> — {@code Export() == "is" + (bIsNot ? "Not" : "") + "KeyPressed("
 *       + renderReference(keyPressed, line) + ")"} — i.e. {@code isKeyPressed(KeyPressed.PF1)}
 *       or {@code isNotKeyPressed(KeyPressed.PF1)}. The two forms are two distinct protected
 *       {@code nacaLib.basePrgEnv.BaseProgram} condition calls
 *       ({@code isKeyPressed(nacaLib.misc.KeyPressed)} / {@code isNotKeyPressed(...)}), selected
 *       by the {@code bIsNot} flag — not a {@code !(...)} wrapping. The condition now renders
 *       through the BMS forms-island binding {@code semantic.forms.CEntityIsKeyPressed ->
 *       recursiveIsKeyPressedEntity}; the template branches on the pure read-only
 *       {@link #isOpposite()} getter to select the {@code is}/{@code isNot} call and reads the
 *       console-key sub-entity through {@link #getKeyPressed()}, which unfolds recursively
 *       through the assembler (REFERENCE role -&gt; {@code recursiveKeyPressedEntity} -&gt;
 *       {@code KeyPressed.<constant>}). Both runtime calls are contracted as
 *       {@code bms.keyPressed.is} / {@code bms.keyPressed.isNot} (runtime-operations.yaml) and
 *       required by the template (template-runtime-requirements.yaml).</li>
 * </ul>
 *
 * <p>The template getters are pure property reads: {@link #isOpposite()} returns the
 * precomputed {@code bIsNot} flag and {@link #getKeyPressed()} returns the already-resolved
 * console-key sub-entity populated by {@link #isKeyPressed}/{@link #isNotKeyPressed} during
 * semantic analysis — no formatting, reference resolution or lowering happens when ST4 accesses
 * them. This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic.
 *
 * @author U930CV
 */
public class CEntityIsKeyPressed extends CBaseEntityCondition
{

	public void isKeyPressed(CDataEntity key)
	{
		keyPressed = key ;
		bIsNot = false ;
	}
	public void isNotKeyPressed(CDataEntity key)
	{
		keyPressed = key ;
		bIsNot = true ;
	}
	protected CDataEntity keyPressed = null ;
	protected boolean bIsNot = false ;

	public int GetPriorityLevel()
	{
		return 7;
	}

	public CBaseEntityCondition GetOppositeCondition()
	{
		CEntityIsKeyPressed is = new CEntityIsKeyPressed();
		is.bIsNot = !bIsNot ;
		is.keyPressed = keyPressed ;
		keyPressed.RegisterValueAccess(is) ;
		return is ;
	}

	/**
	 * Pure read-only getter for the {@code recursiveIsKeyPressedEntity} template: the negation
	 * flag selecting the {@code isKeyPressed}/{@code isNotKeyPressed} runtime call. Precomputed
	 * by {@link #isKeyPressed}/{@link #isNotKeyPressed}; ST4 only reads it.
	 */
	public boolean isOpposite()
	{
		return bIsNot ;
	}

	/**
	 * Pure read-only getter for the {@code recursiveIsKeyPressedEntity} template: the console-key
	 * sub-entity (a {@link CEntityKeyPressed}) rendered recursively through the assembler to
	 * {@code KeyPressed.<constant>}. Resolved during semantic analysis; ST4 only reads it.
	 */
	public CDataEntity getKeyPressed()
	{
		return keyPressed ;
	}

	public void Clear()
	{
		super.Clear();
		keyPressed = null ;
	}

	public boolean ignore()
	{
		return false ;
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return null;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}
	/**
	 * @see semantic.expression.CBaseEntityCondition#GetConditionReference()
	 */
	@Override
	public CDataEntity GetConditionReference()
	{
		return null;
	}
	public void SetConditonReference(CDataEntity e)
	{
		ASSERT(null) ;
	}
}
