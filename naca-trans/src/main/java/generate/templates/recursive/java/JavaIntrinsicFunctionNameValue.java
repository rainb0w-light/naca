package generate.templates.recursive.java;

import java.util.Objects;

/** Atomic COBOL intrinsic name awaiting Java runtime-name formatting by ST4. */
public record JavaIntrinsicFunctionNameValue(String value)
{
    /** Executes the java intrinsic function name value operation. */
    public JavaIntrinsicFunctionNameValue
    {
        Objects.requireNonNull(value, "value");
    }
}
