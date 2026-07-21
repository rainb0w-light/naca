package semantic;

/**
 * Target-neutral kind of a COBOL program. The Java backend maps each kind to a
 * runtime base type (e.g. BATCH -&gt; BatchProgram) in its templates; the semantic
 * layer never carries those target class names.
 */
public enum ProgramKind
{
    BATCH,
    CALLED,
    INCLUDED,
    MAP,
    ONLINE
}
