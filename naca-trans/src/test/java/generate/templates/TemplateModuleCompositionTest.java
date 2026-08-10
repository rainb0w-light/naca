package generate.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

/** Pins the ST4 composition behavior required by the modular template catalog. */
class TemplateModuleCompositionTest
{
    @Test
    void explicitlyImportedDependenciesSupportCrossModuleCalls()
    {
        STGroup common = unloadedGroup("common", """
            group common;
            value(value) ::= <<
            <value; format="upper">
            >>
            """);
        common.load();
        STGroup domain = unloadedGroup("domain", """
            group domain;
            statement(value) ::= "[<value(value)>]"
            """);
        domain.importTemplates(common);
        domain.load();

        STGroup catalog = new STGroup();
        catalog.importTemplates(domain);
        catalog.registerRenderer(String.class, new UpperCaseRenderer());

        ST template = catalog.getInstanceOf("statement");
        assertNotNull(template);
        assertEquals("[VALUE]", template.add("value", "value").render());
    }

    @Test
    void catalogCanResolveTemplatesFromEveryImportedModule()
    {
        STGroup first = unloadedGroup("first", """
            group first;
            first() ::= "first"
            """);
        STGroup second = unloadedGroup("second", """
            group second;
            second() ::= "second"
            """);
        first.load();
        second.load();

        STGroup catalog = new STGroup();
        catalog.importTemplates(first);
        catalog.importTemplates(second);

        assertEquals("first", catalog.getInstanceOf("first").render());
        assertEquals("second", catalog.getInstanceOf("second").render());
    }

    private static STGroup unloadedGroup(String name, String source)
    {
        return new STGroupString(name + ".stg", source, '<', '>');
    }

    private static final class UpperCaseRenderer implements AttributeRenderer<String>
    {
        @Override
        public String toString(String value, String format, Locale locale)
        {
            return "upper".equals(format) ? value.toUpperCase(Locale.ROOT) : value;
        }
    }
}
