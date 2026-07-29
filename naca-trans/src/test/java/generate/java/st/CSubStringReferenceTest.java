package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaSubStringReference;
import semantic.expression.CEntityExprTerminal;
import org.junit.jupiter.api.Test;

class CSubStringReferenceTest
{
    @Test
    void retainsStartAndLengthExpressions()
    {
        CJavaSubStringReference reference = new CJavaSubStringReference(1, null, null);
        reference.SetReference(
            new MockDataEntity(1, "source"),
            new LegacyTerminal(new MockDataEntity(1, "index")),
            new LegacyTerminal(new MockDataEntity(1, "1")));

        assertEquals("subString(source, index, 1)", reference.ExportReference(1));
    }

    private static final class LegacyTerminal extends CEntityExprTerminal
    {
        private LegacyTerminal(MockDataEntity term)
        {
            super(term);
        }

        public String Export()
        {
            return term.ExportReference(getLine());
        }
    }
}
