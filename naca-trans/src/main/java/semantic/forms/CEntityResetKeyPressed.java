/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: the CICS reset-key-pressed action (forget the AID-key state of the
 * last map I/O). It is built by {@link CEntityGetKeyPressed#getSpecialAssignment} when an
 * assignment of {@code SPACE} to the get-key-pressed pseudo-variable lowers from a BMS
 * map-resource program ({@code MOVE SPACE TO KEYPRESSED}).
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaResetKeyPressed}
 * was retired onto the recursive ST4 assembly contract. The backend carried a single output
 * protocol, preserved target-neutrally:
 * <ul>
 *   <li><b>action statement</b> — {@code DoExport} wrote the line {@code resetKeyPressed();}
 *       — the protected no-argument {@code nacaLib.basePrgEnv.BaseProgram.resetKeyPressed()}
 *       call (inherited by generated program subclasses). The statement now renders through
 *       the BMS forms-island binding {@code semantic.forms.CEntityResetKeyPressed ->
 *       recursiveResetKeyPressedEntity}; the template emits the bare {@code resetKeyPressed();}
 *       statement byte-for-byte as the retired backend did (no space before the semicolon,
 *       exactly the legacy {@code writeLine} literal). The call is contracted as
 *       {@code bms.keyPressed.reset} (runtime-operations.yaml) and required by the template
 *       (template-runtime-requirements.yaml).</li>
 * </ul>
 *
 * <p>Like the sibling {@code CEntitySetCursor} action, this entity carries no {@code DoExport}
 * of its own: the statement renders purely through the recursive assembler's REFERENCE role
 * (the program root's executable children), not the reflective legacy {@code invokeExport}
 * traversal. This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic.
 *
 * <p>The action is dead wiring in production: it is only ever built by
 * {@code CEntityGetKeyPressed.GetSpecialAssignment} for a {@code MOVE SPACE TO KEYPRESSED},
 * which no shipped BMS map-resource program performs, so {@code resetKeyPressed();} has no
 * live caller; it is preserved for completeness and proven to still render through the binding.
 *
 * @author U930CV
 */
public class CEntityResetKeyPressed extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntityResetKeyPressed(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }

}
