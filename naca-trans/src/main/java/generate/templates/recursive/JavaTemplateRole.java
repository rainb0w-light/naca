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
    FPAC_REFERENCE
}
