package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityBreak;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondNot;
import semantic.expression.CEntityIsFileEOF;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondNot} direct backend.
 *
 * <p>The ONLY production creation site of that backend was
 * {@link CFPacJavaIsFileEOF#GetOppositeCondition()}: FPac negates an END-OF-FILE test by wrapping
 * it (there is no {@code isNotEof} runtime call). The retired backend's {@code Export()} emitted
 * {@code "!" + CJavaExporter.ExportChildCondition(...)} → {@code !isEof(<name>)}. Post-retirement,
 * {@code GetOppositeCondition()} returns a pure target-neutral {@link CEntityCondNot} wrapping the
 * EOF condition; rendering it reaches the SHARED {@code recursiveCondNotEntity} binding
 * ({@code !(<entity.operand>)}), whose operand re-enters the recursive assembler on the EOF condition.
 *
 * <p>The crux the previous attempt missed: that operand ({@code CFPacJavaIsFileEOF}, the concrete
 * instantiation of the abstract {@link CEntityIsFileEOF}) had NO binding in any manifest, so the
 * operand render failed closed with {@code MissingTemplateRendererException} for every production
 * {@code NOT(EOF)} (and the same gap hit the positive EOF test the FPac AT loop lowers directly).
 * This slice adds the {@code semantic.expression.CEntityIsFileEOF -> recursiveIsFileEOFEntity}
 * runtime superclass alias (in {@code semantic-runtime-bindings.properties}, loaded into both
 * REFERENCE and FPAC_REFERENCE; the type is abstract, so it cannot live in the concrete inventory
 * manifest {@code semantic-bindings.properties} that the final-architecture test pins). The template
 * emits {@code isEof(<name>)} — reproducing the {@code CFPacJavaIsFileEOF.Export()} operand output
 * via the shared {@code recursiveFileDescriptorEntity} binding, exactly as {@code recursiveCloseFileEntity}
 * does — so the negated condition renders {@code !(isEof(<name>))}, the canonical recursive shape
 * (semantically identical to the legacy {@code !isEof(<name>)}). The {@code isEof} predicate is the
 * protected {@code nacaLib.fpacPrgEnv.FPacProgram.isEof(BaseFileDescriptor)} call (contract operation
 * {@code fpac.file.eof}).
 *
 * <p>These tests drive the EOF condition through the SAME operand type the retired backend wrapped in
 * production ({@code CFPacJavaIsFileEOF}, built by {@code CJavaFPacEntityFactory.NewEntityIsFileEOF}
 * exactly as {@code parser/FPac/elements/CFPacAt} lowers the AT-loop EOF test) — never a stand-in
 * already-bound type — and render the wrapping NOT end-to-end, so the production path is proven green.
 */
class CFPacJavaCondNotRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    /** A named descriptor whose formatted name needs no catalog for this focused fixture. */
    private static CEntityFileDescriptor fileDescriptor(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override
            protected void RegisterMySelfToCatalog()
            {
                // No catalog registration is needed for this focused reference fixture.
            }
        };
    }

    private static String render(Object condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    private CEntityIsFileEOF eof()
    {
        return new CJavaFPacEntityFactory(catalog, null)
            .NewEntityIsFileEOF(fileDescriptor("CUSTOMER-FILE"));
    }

    /** The EOF operand renders through the recursive assembler via its superclass alias. */
    @Test
    void rendersEofOperandThroughRecursiveAssembler()
    {
        CEntityIsFileEOF condition = eof();
        // The factory hands back the concrete FPac instantiation the retired backend wrapped.
        assertInstanceOf(CFPacJavaIsFileEOF.class, condition);
        assertEquals("isEof(CUSTOMER_FILE)", render(condition));
    }

    /**
     * The retired backend's only job: negating EOF. {@code GetOppositeCondition()} now returns a
     * PURE {@link CEntityCondNot} (exactly that class, no {@code generate.fpacjava} subclass), and
     * rendering it through the recursive assembler emits {@code !(isEof(<name>))} — the operand
     * unfolds through the new {@code recursiveIsFileEOFEntity} binding instead of crashing with
     * {@code MissingTemplateRendererException} as it did before this slice.
     */
    @Test
    void rendersNotEofThroughRecursiveAssembler()
    {
        CBaseEntityCondition opposite = eof().GetOppositeCondition();
        assertEquals(CEntityCondNot.class, opposite.getClass());
        assertEquals("!(isEof(CUSTOMER_FILE))", render(opposite));
    }

    /** The negation round-trips: the opposite of {@code NOT(EOF)} is the EOF condition again. */
    @Test
    void notEofOppositeUnwrapsToEof()
    {
        CBaseEntityCondition opposite = eof().GetOppositeCondition();
        CBaseEntityCondition operand =
            assertInstanceOf(CEntityCondNot.class, opposite).getOperand();
        assertInstanceOf(CFPacJavaIsFileEOF.class, operand);
        assertEquals("isEof(CUSTOMER_FILE)", render(operand));
        assertEquals("isEof(CUSTOMER_FILE)", render(opposite.GetOppositeCondition()));
    }

    /**
     * End-to-end production lowering mirroring {@code parser/FPac/elements/CFPacAt}: the factory
     * builds the EOF condition ({@code NewEntityIsFileEOF}), the AT loop negates it via
     * {@code GetOppositeCondition()} into a pure {@link CEntityCondNot}, and the enclosing {@code if}
     * (NewEntityCondition + a real {@code break;} then-block) renders through the shared
     * {@code recursiveIfEntity} binding. The nested {@code recursiveCondNotEntity} →
     * {@code recursiveIsFileEOFEntity} render is exactly the production path that crashed before the
     * EOF operand binding existed.
     */
    @Test
    void fpacIfLowersNotEofThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CBaseEntityCondition notEof =
            factory.NewEntityIsFileEOF(fileDescriptor("CUSTOMER-FILE")).GetOppositeCondition();
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(notEof, thenBloc, null);

        assertEquals("if (!(isEof(CUSTOMER_FILE))) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }
}
