package semantic;

/**
 * Target-neutral capability of a program, established during semantic analysis
 * (from the catalog's import declarations). The Java backend maps each
 * capability to optional imports in its templates; the semantic layer exposes
 * only this enum, never the legacy {@code "SQL"}/{@code "MAP"}/{@code "KEYPRESSED"}
 * marker strings or Java import text.
 */
public enum ProgramCapability
{
    SQL,
    MAP_SUPPORT,
    KEY_PRESSED
}
