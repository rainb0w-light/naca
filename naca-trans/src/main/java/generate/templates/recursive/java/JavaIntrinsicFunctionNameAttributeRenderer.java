package generate.templates.recursive.java;

import java.util.Locale;
import org.stringtemplate.v4.AttributeRenderer;

/** Maps a COBOL intrinsic name to its Java runtime method name. */
public final class JavaIntrinsicFunctionNameAttributeRenderer
    implements AttributeRenderer<JavaIntrinsicFunctionNameValue>
{
    @Override
    public String toString(
        JavaIntrinsicFunctionNameValue functionName,
        String formatString,
        Locale locale)
    {
        return functionName.value().toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
