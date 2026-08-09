package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CSubStringAttributReference;
import semantic.expression.CEntityExprTerminal;
import org.junit.jupiter.api.Test;

class CSubStringReferenceTest
{
    @Test
    void retainsStartAndLengthExpressions()
    {
        CSubStringAttributReference reference = new CSubStringAttributReference(1, null);
        reference.SetReference(
            new MockDataEntity(1, "source"),
            new LegacyTerminal(new MockDataEntity(1, "index")),
            new LegacyTerminal(new MockDataEntity(1, "1")));

        assertEquals("subString(source, index, 1)", TemplateLoader.getRecursiveAssembler()
            .renderRoot(reference, JavaTemplateRole.REFERENCE));
    }

    private static final class LegacyTerminal extends CEntityExprTerminal
    {
        private LegacyTerminal(MockDataEntity term)
        {
            super(term);
        }

    }
}
