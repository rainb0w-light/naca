package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import semantic.Verbs.CEntityAssignWithAccessor;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityAssignWithAccessor (MOVE to a field with a
 * write accessor). Renders through the recursive assembler
 * (CEntityAssignWithAccessor=recursiveAssignWithAccessorEntity). The accessor
 * invocation (including the FILL ALL {@code xxxAll(...)} rewrite) is computed as
 * a ready semantic string, matching the direct generator.
 */
public class CJavaAssignWithAccessorST extends CEntityAssignWithAccessor {
    public CJavaAssignWithAccessorST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat);
        setLanguageExporter(out);
    }

    /** Ready accessor call, e.g. {@code setXxx(value)} or {@code setXxxAll(value)}. */
    public String getAccessorInvocation() {
        String val = value != null ? value.ExportReference(getLine()) : "";
        String out = reference.ExportWriteAccessorTo(val);
        if (isfillAll) {
            out = out.replaceFirst("([^\\(]*)(\\(.*)", "$1All$2");
        }
        return out;
    }

    @Override
    protected void DoExport() {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(this);
        for (String line : rendered.split("\n", -1)) {
            if (!line.isEmpty()) {
                WriteLine(line);
            }
        }
    }
}
