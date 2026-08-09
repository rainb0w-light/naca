package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityClass;
import semantic.CEntityDataSection;
import semantic.CEntityFileDescriptor;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;

/** Retirement proof for the FPac file-descriptor direct backend. */
class CFPacJavaFileDescriptorRetirementTest
{
    private static String render(CEntityFileDescriptor descriptor)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(descriptor, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityFileDescriptor.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityFileDescriptor(1, "CUST-FILE").getClass());
    }

    @Test
    void assemblerRendersTheFpacDeclarationInsteadOfTheCobolDeclaration()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityFileDescriptor descriptor =
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityFileDescriptor(1, "CUST-FILE");

        String rendered = render(descriptor);
        assertEquals(
            "FPacFileDescriptor cust_File = declare.fpacFile(\"CUST-FILE\").file() ;",
            rendered);
        assertFalse(rendered.contains("declare.file("), rendered);
    }

    @Test
    void assemblerRendersTheOptionalOutputBufferFiller()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityFileDescriptor descriptor =
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityFileDescriptor(1, "OPF");
        descriptor.setOutputBufferInitialValue(
            new CEntityString(catalog, " ".toCharArray()));

        assertEquals(
            "FPacFileDescriptor opf = declare.fpacFile(\"OPF\")"
                + ".fillOutputBuffer(\" \").file() ;",
            render(descriptor));
    }

    @Test
    void productionRootRendersThePureDeclarationSubtreeThroughTheAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);
        CEntityClass root = factory.NewEntityClass(1, "PROG");
        CEntityDataSection data = factory.NewEntityDataSection(2, "DeclarationSection");
        data.AddChild(factory.NewEntityFileDescriptor(3, "CUST-FILE"));
        root.AddChild(data);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(root, JavaTemplateRole.FPAC_ROOT);
        assertTrue(rendered.contains(
            "FPacFileDescriptor cust_File = declare.fpacFile(\"CUST-FILE\").file() ;"),
            rendered);
        assertFalse(rendered.contains("DataSection "), rendered);
        assertFalse(rendered.contains("declare.file("), rendered);
    }
}
