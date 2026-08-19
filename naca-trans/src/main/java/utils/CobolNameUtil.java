package utils;

/** Shared Java identifier formatting for COBOL procedure and paragraph names. */
public class CobolNameUtil
{
    /** Executes the fix java name operation. */
    public static String fixJavaName(String name)
    {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    /** Executes the format procedure name operation. */
    public static String formatProcedureName(String name)
    {
        String formatted = name.replace('-', '_').replace('#', '$');
        if (!formatted.isEmpty() && Character.isDigit(formatted.charAt(0)))
        {
            return "$" + formatted;
        }
        return formatted;
    }
}
