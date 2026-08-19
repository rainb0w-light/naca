package semantic;

import utils.CObjectCatalog;

/**
 * FPac semantic marker for an unresolved source identifier.
 *
 * <p>FPac deliberately keeps the legacy {@code [UNDEFINED]} diagnostic token in
 * generated expressions, while the COBOL pipeline fails closed by rendering an
 * unresolved reference as an empty fragment. The distinction belongs to source
 * semantics; the Java spelling remains in the ST4 binding.
 */
public final class CFPacUnknownReference extends CEntityUnknownReference
{
    /** Creates a new cfpac unknown reference instance. */
    public CFPacUnknownReference(int line, String name, CObjectCatalog catalog)
    {
        super(line, name, catalog);
    }
}
