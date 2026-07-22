package generate.templates.recursive.java;

import java.util.Objects;

/** Atomic value formatted by ST4's attribute-renderer mechanism. */
public final class JavaStringLiteralValue
{
    private final String value;

    public JavaStringLiteralValue(String value)
    {
        this.value = Objects.requireNonNull(value, "value");
    }

    public String value()
    {
        return value;
    }
}
