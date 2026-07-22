package generate.templates.recursive.java;

import java.util.Objects;

/** Raw COBOL name formatted atomically by the Java identifier renderer. */
public final class JavaIdentifierValue
{
    private final String value;

    public JavaIdentifierValue(String value)
    {
        this.value = Objects.requireNonNull(value, "value");
    }

    public String value()
    {
        return value;
    }
}
