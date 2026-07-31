package generate.templates.recursive;

/** Semantic role used to select a target template binding. */
public enum JavaTemplateRole
{
    REFERENCE,
    DECLARATION,
    ROOT,
    /**
     * Independent FPac pipeline reference rendering. Reuses the shared reference
     * manifest but overrides the few semantic types whose FPac lowering genuinely
     * differs from COBOL — e.g. {@code semantic.Verbs.CEntityCallFunction}, which
     * COBOL renders as a PERFORM but FPac renders as a direct {@code name();} call
     * to a generated paragraph method. Keeping the override in a dedicated role
     * leaves the frozen COBOL REFERENCE binding untouched.
     */
    FPAC_REFERENCE,
    /**
     * Independent FPac pipeline root rendering. The FPac program root shares the
     * target-neutral {@code semantic.CEntityClass} with COBOL, but wraps it
     * differently: {@code import nacaLib.fpacPrgEnv.* ;} + {@code public class NAME
     * extends FPacProgram} with an UPPERCASE class name. The COBOL {@code ROOT} role
     * binds {@code semantic.CEntityClass} to {@code javaProgramRoot} (a
     * {@code BatchProgram}/{@code CalledProgram}/... wrapper), which would not compile
     * for FPac. This dedicated role consults ONLY the FPac root manifest
     * ({@code semantic-fpac-root-bindings.properties}), so the frozen COBOL ROOT
     * binding stays untouched and the shared semantic type maps to one wrapper per
     * pipeline. Children of an FPAC_ROOT render under {@link #FPAC_REFERENCE}.
     */
    FPAC_ROOT
}
