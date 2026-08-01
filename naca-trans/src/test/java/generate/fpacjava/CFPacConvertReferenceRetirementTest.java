package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import semantic.CEntityStructure;
import semantic.CSubStringAttributReference;
import semantic.Verbs.CEntityConvertReference;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaConvertReference}
 * direct backend. {@code CEntityConvertReference} is an FPac-only data reference (COBOL's
 * {@code CJavaEntityFactory.NewEntityConvert} throws "not implemented"), so
 * {@link CJavaFPacEntityFactory#NewEntityConvert(int)} must hand back the pure, now
 * concrete {@link CEntityConvertReference} (no {@code generate.fpacjava} subclass). The
 * conversion mode and wrapped reference are populated by the parser via
 * {@code convertToPacked}/{@code convertToAlphaNum}; rendering reaches the recursive ST4
 * assembler through the {@link LegacyDataRenderer} bridge — because the pure entity declares
 * no {@code ExportReference} override, the bridge's reflective lookup falls through to the
 * new {@code semantic.Verbs.CEntityConvertReference -> recursiveFPacConvertReferenceEntity}
 * binding (REFERENCE role).
 *
 * <p>The template reproduces, byte for byte, the three shapes the deleted backend's
 * {@code ExportReference(int)} emitted:
 * <ul>
 *   <li>no conversion: the wrapped reference alone;</li>
 *   <li>conversion of a reference that has accessors: {@code <reference>P}/{@code <reference>X};</li>
 *   <li>conversion of a reference without accessors: the UNCLOSED call head
 *       {@code bufferP(<reference>}/{@code bufferX(<reference>}.</li>
 * </ul>
 * The unclosed paren is deliberate: in production this entity is always wrapped by a
 * {@code CSubStringAttributReference}, whose renderer sees the {@code "("} and appends
 * {@code ", start, length)"}, completing a real
 * {@code FPacProgram.bufferP/bufferX(FPacFileDescriptor, int, int)} call. The assertions
 * below encode the retired backend's exact output formula (computed from the same inner
 * {@code LegacyDataRenderer.renderReference} rendering the backend used), so a green test
 * proves byte-parity with the deleted backend by construction.
 */
class CFPacConvertReferenceRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityConvertReference.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityConvert(1).getClass());
    }

    /**
     * Live production branch — packed conversion of a reference without accessors (an
     * ordinary field/structure/file buffer, which all report {@code HasAccessors()==false}).
     * The retired backend emitted {@code "bufferP(" + renderReference(reference)}; the
     * recursive assembler now renders the identical UNCLOSED call head, which the wrapping
     * substring completes (see {@link #bufferCallHeadComposesWithWrappingSubString()}).
     */
    @Test
    void productionBridgeRendersPackedBufferConvertReference()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure field = new CEntityStructure(1, "MY-FIELD", catalog, "01");
        CEntityConvertReference conv = factory.NewEntityConvert(1);
        conv.convertToPacked(field);

        String rendered = LegacyDataRenderer.renderReference(conv, 1);
        String reference = LegacyDataRenderer.renderReference(field, 1);
        assertEquals("bufferP(" + reference, rendered);
    }

    /**
     * Live production branch — alphanumeric conversion of a reference without accessors:
     * the retired backend emitted {@code "bufferX(" + renderReference(reference)}; the
     * template renders the identical unclosed {@code bufferX(} call head.
     */
    @Test
    void productionBridgeRendersAlphaNumBufferConvertReference()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure field = new CEntityStructure(1, "MY-FIELD", catalog, "01");
        CEntityConvertReference conv = factory.NewEntityConvert(1);
        conv.convertToAlphaNum(field);

        String rendered = LegacyDataRenderer.renderReference(conv, 1);
        String reference = LegacyDataRenderer.renderReference(field, 1);
        assertEquals("bufferX(" + reference, rendered);
    }

    /**
     * Totality for the accessor shape (fires for FPac form-field / environment-variable
     * references, which report {@code HasAccessors()==true}): the retired backend emitted
     * {@code renderReference(reference) + "P"}; the template renders the identical suffixed
     * reference. The inner reference is a field that carries accessors.
     */
    @Test
    void rendersAccessorConvertReferenceThroughAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure accessorField = new AccessorField(catalog);
        CEntityConvertReference conv = factory.NewEntityConvert(1);
        conv.convertToPacked(accessorField);

        String rendered = LegacyDataRenderer.renderReference(conv, 1);
        String reference = LegacyDataRenderer.renderReference(accessorField, 1);
        assertEquals(reference + "P", rendered);
    }

    /**
     * End-to-end proof of the central composition protocol. The FPac parser always wraps a
     * positioned conversion reference in a {@code CSubStringAttributReference}
     * (CFPacMove/CFPacConvert/CFPacArithmeticOperation). That wrapper still lowers through
     * its legacy backend, which inspects the string returned for its base reference: because
     * {@code conv.HasAccessors()==true} it takes its accessor branch, and because the
     * migrated convert reference emits the unclosed {@code bufferP(} head, the wrapper
     * detects the {@code "("} and appends {@code ", start, length)"} — completing the real
     * {@code bufferP(fd, start, length)} runtime call. This is exactly what the deleted
     * backend produced, so the migration leaves the composed output unchanged.
     */
    @Test
    void bufferCallHeadComposesWithWrappingSubString()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure field = new CEntityStructure(1, "MY-FIELD", catalog, "01");
        CEntityConvertReference conv = factory.NewEntityConvert(1);
        conv.convertToPacked(field);
        // The convert reference itself must carry accessors so the wrapping substring takes
        // its accessor branch and completes the bufferP(...) call head (mirrors the retired
        // backend's HasAccessors() override).
        assertTrue(conv.HasAccessors(),
            "convert reference must report HasAccessors so the substring wrapper composes it");

        CSubStringAttributReference sub = factory.NewEntitySubString(1);
        sub.SetReference(conv, number("2"), number("5"));

        String composed = LegacyDataRenderer.renderReference(sub, 1);
        String reference = LegacyDataRenderer.renderReference(field, 1);
        String start = LegacyDataRenderer.renderReference(number("2"), 1);
        String length = LegacyDataRenderer.renderReference(number("5"), 1);
        assertEquals("bufferP(" + reference + ", " + start + ", " + length + ")", composed);
        assertTrue(composed.endsWith(")"),
            "the wrapping substring must close the bufferP call head: " + composed);
    }

    private CBaseEntityExpression number(String value)
    {
        return new CEntityExprTerminal(new CEntityNumber(catalog, value)) {};
    }

    /**
     * A field reference that carries accessors (as an FPac form field or environment
     * variable does). Resolves through the inherited {@code semantic.CEntityStructure ->
     * dataReferenceEntity} binding; only {@code HasAccessors()} is overridden to drive the
     * convert reference's accessor shape.
     */
    private static final class AccessorField extends CEntityStructure
    {
        AccessorField(CObjectCatalog catalog)
        {
            super(1, "MY-ACCESSOR", catalog, "01");
        }

        @Override
        public boolean HasAccessors()
        {
            return true;
        }
    }
}
