package generate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import semantic.CBaseLanguageEntity;

/**
 * Test-only frozen compatibility renderer used by parity fixtures.
 *
 * <p>The COBOL/SQL/CICS pipeline renders semantic trees through the recursive
 * ST4 assembler. Legacy generators still need an imperative output controller,
 * but that controller and its traversal protocol belong here rather than in the
 * target-neutral semantic base class.
 */
public final class LegacyLanguageRenderer
{
    private LegacyLanguageRenderer()
    {
    }

    /** Executes the bind operation. */
    public static void bind(
        CBaseLanguageEntity entity, CBaseLanguageExporter output)
    {
        if (entity == null)
        {
            return;
        }
        LanguageArtifactOutputRegistry.register(entity, output);
        for (CBaseLanguageEntity child : entity.getChildren())
        {
            bind(child, output);
        }
    }

    /** Executes the output operation. */
    public static CBaseLanguageExporter output(CBaseLanguageEntity entity)
    {
        return LanguageArtifactOutputRegistry.get(entity);
    }

    /** Executes the start export operation. */
    public static void startExport(CBaseLanguageEntity entity)
    {
        invokeExport(entity);
        CBaseLanguageExporter output = requireOutput(entity);
        output.closeOutput();
    }

    /** Executes the invoke export operation. */
    public static void invokeExport(CBaseLanguageEntity entity)
    {
        invokeExport(entity, null);
    }

    /**
     * Renders one child of a legacy pipeline (FPac/BMS) subtree. A node that still
     * carries a direct {@code DoExport} backend is driven reflectively as before. A
     * node that has been retired onto the declarative bindings (no {@code DoExport})
     * is rendered through the recursive ST4 assembler with the pipeline's
     * {@code fallbackRole} and written to the bound legacy output — exactly the bridge
     * {@link LegacyExpressionRenderer#render} provides for expressions. When
     * {@code fallbackRole} is {@code null} the historical behavior is preserved (a
     * backend-less node is left unrendered), so BMS callers that have not opted in are
     * unaffected.
     */
    public static void invokeExport(
        CBaseLanguageEntity entity,
        generate.templates.recursive.JavaTemplateRole fallbackRole)
    {
        if (entity == null)
        {
            return;
        }
        Method method = findExportMethod(entity.getClass());
        if (method == null)
        {
            if (fallbackRole != null)
            {
                renderViaAssembler(entity, fallbackRole);
            }
            return;
        }
        try
        {
            method.setAccessible(true);
            method.invoke(entity);
        }
        catch (IllegalAccessException error)
        {
            throw new IllegalStateException(
                "Cannot access legacy renderer for " + entity.getClass().getName(),
                error);
        }
        catch (InvocationTargetException error)
        {
            Throwable cause = error.getCause();
            if (cause instanceof RuntimeException runtime)
            {
                throw runtime;
            }
            throw new IllegalStateException(
                "Legacy renderer failed for " + entity.getClass().getName(),
                cause);
        }
    }

    /** Exports the children. */
    public static void exportChildren(
        CBaseLanguageEntity entity, boolean includeIgnored)
    {
        exportChildren(entity, includeIgnored, null);
    }

    /**
     * Renders a legacy container's children, routing any backend-less (retired) child
     * through the recursive ST4 assembler with {@code fallbackRole}. See
     * {@link #invokeExport(CBaseLanguageEntity, generate.templates.recursive.JavaTemplateRole)}.
     */
    public static void exportChildren(
        CBaseLanguageEntity entity, boolean includeIgnored,
        generate.templates.recursive.JavaTemplateRole fallbackRole)
    {
        for (CBaseLanguageEntity child : entity.getChildren())
        {
            if (includeIgnored || !child.ignore())
            {
                invokeExport(child, fallbackRole);
            }
        }
    }

    /**
     * Flattens a retired semantic node through the single recursive assembler and writes
     * the result to the node's bound legacy output controller. The assembler's
     * {@code renderRoot} is the only ST-tree flattening point; this bridge merely feeds
     * it a pipeline-specific role and forwards the produced source line.
     */
    private static void renderViaAssembler(
        CBaseLanguageEntity entity,
        generate.templates.recursive.JavaTemplateRole role)
    {
        String rendered = generate.templates.TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, role);
        writeLine(entity, rendered);
    }

    /** Writes the comment. */
    public static void writeComment(CBaseLanguageEntity entity, String text)
    {
        requireOutput(entity).WriteComment(text, entity.getLine());
    }

    /** Writes the line. */
    public static void writeLine(CBaseLanguageEntity entity, String text)
    {
        requireOutput(entity).WriteLine(text, entity.getLine());
    }

    /** Writes the line. */
    public static void writeLine(
        CBaseLanguageEntity entity, String text, int line)
    {
        requireOutput(entity).WriteLine(text, line);
    }

    /** Writes the eol. */
    public static void writeEol(CBaseLanguageEntity entity)
    {
        requireOutput(entity).WriteEOL(entity.getLine());
    }

    /** Writes the word. */
    public static void writeWord(CBaseLanguageEntity entity, String text)
    {
        requireOutput(entity).WriteWord(text, entity.getLine());
    }

    /** Writes the word. */
    public static void writeWord(
        CBaseLanguageEntity entity, String text, int line)
    {
        requireOutput(entity).WriteWord(text, line);
    }

    /** Writes the long string. */
    public static void writeLongString(
        CBaseLanguageEntity entity, String text)
    {
        requireOutput(entity).WriteLongString(text, entity.getLine());
    }

    /** Executes the start block operation. */
    public static void startBlock(CBaseLanguageEntity entity)
    {
        requireOutput(entity).StartBloc();
    }

    /** Executes the end block operation. */
    public static void endBlock(CBaseLanguageEntity entity)
    {
        requireOutput(entity).EndBloc();
    }

    /** Executes the format identifier operation. */
    public static String formatIdentifier(
        CBaseLanguageEntity entity, String identifier)
    {
        CBaseLanguageExporter output = output(entity);
        if (output != null)
        {
            return output.FormatIdentifier(identifier);
        }
        return identifier.replace('-', '_').replace('#', '$');
    }

    private static CBaseLanguageExporter requireOutput(CBaseLanguageEntity entity)
    {
        CBaseLanguageExporter output = output(entity);
        if (output == null)
        {
            throw new IllegalStateException(
                "No legacy output controller bound to "
                    + entity.getClass().getName());
        }
        return output;
    }

    private static Method findExportMethod(Class<?> type)
    {
        for (Class<?> current = type;
            current != null && current != CBaseLanguageEntity.class;
            current = current.getSuperclass())
        {
            try
            {
                return current.getDeclaredMethod("DoExport");
            }
            catch (NoSuchMethodException ignored)
            {
                // Continue through the legacy generator hierarchy.
            }
        }
        return null;
    }
}
