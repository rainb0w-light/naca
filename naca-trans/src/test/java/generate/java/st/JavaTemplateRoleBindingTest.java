package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.fixtures.LegacyAttributeFixture;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import generate.templates.recursive.MissingTemplateRendererException;
import org.junit.jupiter.api.Test;
import semantic.CEntityClass;
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
        CEntityClass program = new CEntityClass(1, "MYPROG", catalog());
        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(program, JavaTemplateRole.ROOT);
        // ROOT resolves CEntityClass to the real javaProgramRoot template, which
        // assembles the compilation unit (imports + class declaration).
        assertTrue(rendered.contains("public class Myprog extends BatchProgram"), rendered);
        assertTrue(rendered.contains("import nacaLib.program.* ;"), rendered);
    }

    @Test
    void declarationRoleHitsTheDeclarationManifest()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        LegacyAttributeFixture attr = new LegacyAttributeFixture(1, "WS-X", catalog(), exporter);
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
        LegacyAttributeFixture attr = new LegacyAttributeFixture(1, "WS-X", catalog(), exporter);
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
        LegacyAttributeFixture attr = new LegacyAttributeFixture(1, "WS-X", catalog(), exporter);
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
        CEntityClass program = new CEntityClass(1, "MYPROG", catalog());
        // The no-arg renderRoot defaults to REFERENCE (default manifest). A
        // program root (CEntityClass) is not in the default manifest, so an
        // artifact writer that forgets to pass ROOT explicitly fails closed
        // instead of silently emitting a reference. This is the guard that lets
        // us delete the no-arg overload (or restore its ROOT default) once the
        // transitional callers pass REFERENCE explicitly.
        assertThrows(MissingTemplateRendererException.class, () ->
            TemplateLoader.getRecursiveAssembler().renderRoot(program, JavaTemplateRole.REFERENCE));
    }
}
