package generate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import semantic.CBaseLanguageEntity;

/**
 * Compatibility boundary for the excluded FPac and BMS direct generators.
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

	public static void bind(
		CBaseLanguageEntity entity, CBaseLanguageExporter output)
	{
		if (entity == null)
		{
			return;
		}
		LegacyLanguageOutputRegistry.register(entity, output);
		for (CBaseLanguageEntity child : entity.getChildren())
		{
			bind(child, output);
		}
	}

	public static CBaseLanguageExporter output(CBaseLanguageEntity entity)
	{
		return LegacyLanguageOutputRegistry.get(entity);
	}

	public static void startExport(CBaseLanguageEntity entity)
	{
		invokeExport(entity);
		CBaseLanguageExporter output = requireOutput(entity);
		output.closeOutput();
	}

	public static void invokeExport(CBaseLanguageEntity entity)
	{
		if (entity == null)
		{
			return;
		}
		Method method = findExportMethod(entity.getClass());
		if (method == null)
		{
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

	public static void exportChildren(
		CBaseLanguageEntity entity, boolean includeIgnored)
	{
		for (CBaseLanguageEntity child : entity.getChildren())
		{
			if (includeIgnored || !child.ignore())
			{
				invokeExport(child);
			}
		}
	}

	public static void writeComment(CBaseLanguageEntity entity, String text)
	{
		requireOutput(entity).WriteComment(text, entity.getLine());
	}

	public static void writeLine(CBaseLanguageEntity entity, String text)
	{
		requireOutput(entity).WriteLine(text, entity.getLine());
	}

	public static void writeLine(
		CBaseLanguageEntity entity, String text, int line)
	{
		requireOutput(entity).WriteLine(text, line);
	}

	public static void writeEol(CBaseLanguageEntity entity)
	{
		requireOutput(entity).WriteEOL(entity.getLine());
	}

	public static void writeWord(CBaseLanguageEntity entity, String text)
	{
		requireOutput(entity).WriteWord(text, entity.getLine());
	}

	public static void writeWord(
		CBaseLanguageEntity entity, String text, int line)
	{
		requireOutput(entity).WriteWord(text, line);
	}

	public static void writeLongString(
		CBaseLanguageEntity entity, String text)
	{
		requireOutput(entity).WriteLongString(text, entity.getLine());
	}

	public static void startBlock(CBaseLanguageEntity entity)
	{
		requireOutput(entity).StartBloc();
	}

	public static void endBlock(CBaseLanguageEntity entity)
	{
		requireOutput(entity).EndBloc();
	}

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
