package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CStringExporter;
import generate.java.CJavaClass;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import generate.templates.recursive.MissingTemplateRendererException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Step 7a/7b: lock the fail-closed-by-design behavior of the recursive assembler
 * that the production exit now uses. When a semantic node has no ST binding for
 * the requested role, rendering must throw {@link MissingTemplateRendererException}
 * — never silently fall back to the direct generator — and (step 7b) the error
 * must name the semantic type that needs a binding, so fail-closed is actionable
 * rather than a black-box failure.
 *
 * <p>Note on CICS/SQL: today those constructs are dropped or rejected during
 * parsing (before the semantic tree is rendered), so a CICS/SQL sample does not
 * itself reach the assembler's fail-closed path — migrating them is a separate
 * parser+binding effort. This test locks the assembler guarantee directly: any
 * node that DOES reach rendering without a binding fails closed with a named
 * binding, and the production exit propagates that (see TranspilerService).
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class FailClosedBehaviorTest {

    private static CObjectCatalog catalog() {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    @Test
    @DisplayName("A node with no binding for the requested role fails closed and names the semantic type")
    void missingBindingFailsClosedAndNamesTheSemanticType() {
        CJavaClass program = new CJavaClass(1, "PROG", catalog(), new CStringExporter());

        // CEntityClass has a ROOT binding but none in the DECLARATION manifest, so
        // rendering it in the DECLARATION role must fail closed (no fallback).
        MissingTemplateRendererException e = assertThrows(
            MissingTemplateRendererException.class,
            () -> TemplateLoader.getRecursiveAssembler()
                .renderRoot(program, JavaTemplateRole.DECLARATION));

        assertTrue(e.getMessage().contains("missing ST binding"),
            "fail-closed error must say it is a missing binding; got: " + e.getMessage());
        assertTrue(e.getMessage().contains("semantic.CEntityClass"),
            "fail-closed error must name the semantic type needing a binding; got: " + e.getMessage());
    }
}
