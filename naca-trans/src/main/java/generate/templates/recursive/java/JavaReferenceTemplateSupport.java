package generate.templates.recursive.java;

import generate.templates.recursive.JavaTemplateAssembler;
import org.stringtemplate.v4.ST;
import semantic.CBaseExternalEntity;

final class JavaReferenceTemplateSupport
{
    private JavaReferenceTemplateSupport()
    {
    }

    static ST dataReference(
        JavaTemplateAssembler assembler,
        String rawIdentifier,
        CBaseExternalEntity qualifier)
    {
        return assembler.template("dataReference")
            .add("identifier", new JavaIdentifierValue(rawIdentifier))
            .add("qualifier", assembler.renderOptional(qualifier));
    }
}
