package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaAttribute;
import generate.java.CJavaClass;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import generate.templates.recursive.MissingTemplateRendererException;
import org.junit.jupiter.api.Test;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Step 2: the assembler routes each role to its own manifest with no fallback.
 * ROOT consults only {@code semantic-root-bindings.properties}, DECLARATION only
 * {@code semantic-declaration-bindings.properties}, and REFERENCE the default
 * (concrete + runtime) manifest. A root type without a binding fails closed.
 */
class JavaTemplateRoleBindingTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    @Test
    void rootRoleHitsTheRootManifest()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaClass program = new CJavaClass(1, "MYPROG", catalog(), exporter);
        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(program, JavaTemplateRole.ROOT);
        assertTrue(rendered.contains("javaProgramRoot placeholder for MYPROG"), rendered);
    }

    @Test
    void declarationRoleHitsTheDeclarationManifest()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-X", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);
        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(attr, JavaTemplateRole.DECLARATION);
        assertTrue(rendered.contains("Var WS_X = declare.level(05).picX(4)"), rendered);
    }

    @Test
    void referenceRoleHitsTheDefaultManifest()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-X", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);
        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(attr, JavaTemplateRole.REFERENCE);
        assertEquals("WS_X", rendered);
    }

    @Test
    void rootRoleDoesNotFallBackToTheDefaultManifest()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-X", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);
        // CEntityAttribute has no root binding and ROOT must not fall back to the
        // default manifest, so this fails closed.
        assertThrows(MissingTemplateRendererException.class, () ->
            TemplateLoader.getRecursiveAssembler().renderRoot(attr, JavaTemplateRole.ROOT));
    }

    @Test
    void programArtifactCannotBeGeneratedWithoutExplicitRootRole()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaClass program = new CJavaClass(1, "MYPROG", catalog(), exporter);
        // The no-arg renderRoot defaults to REFERENCE (default manifest). A
        // program root (CEntityClass) is not in the default manifest, so an
        // artifact writer that forgets to pass ROOT explicitly fails closed
        // instead of silently emitting a reference. This is the guard that lets
        // us delete the no-arg overload (or restore its ROOT default) once the
        // transitional callers pass REFERENCE explicitly.
        assertThrows(MissingTemplateRendererException.class, () ->
            TemplateLoader.getRecursiveAssembler().renderRoot(program));
    }
}
