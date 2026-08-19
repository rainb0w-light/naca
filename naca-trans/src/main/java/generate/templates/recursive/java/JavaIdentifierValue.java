package generate.templates.recursive.java;

import java.util.Objects;

/** Raw COBOL name formatted atomically by the Java identifier renderer. */
public final class JavaIdentifierValue
{
    private final String value;

    /** Creates a new java identifier value instance. */
    public JavaIdentifierValue(String value)
    {
        this.value = Objects.requireNonNull(value, "value");
    }

    /** Executes the value operation. */
    public String value()
    {
        return value;
    }
}
