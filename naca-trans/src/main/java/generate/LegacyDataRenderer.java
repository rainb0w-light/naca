package generate;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import semantic.CDataEntity;

/**
 * Compatibility boundary for data rendering in excluded direct generators.
 *
 * <p>The COBOL/SQL/CICS path always uses the recursive assembler. Older
 * FPac/BMS subclasses may temporarily retain local reference/accessor methods;
 * reflection keeps those protocols out of the target-neutral semantic base.
 */
public final class LegacyDataRenderer
{
	private LegacyDataRenderer()
	{
	}

	public static String renderReference(CDataEntity entity, int line)
	{
		if (entity == null)
		{
			return "[UNDEFINED]" ;
		}
		String legacy = invoke(entity, "ExportReference",
			new Class<?>[] {int.class}, new Object[] {line}) ;
		if (legacy != null)
		{
			return legacy ;
		}
		return TemplateLoader.getRecursiveAssembler()
			.renderRoot(entity, JavaTemplateRole.REFERENCE) ;
	}

	public static String renderWriteAccessor(CDataEntity entity, String value)
	{
		if (entity == null)
		{
			return null ;
		}
		return invoke(entity, "ExportWriteAccessorTo",
			new Class<?>[] {String.class}, new Object[] {value}) ;
	}

	private static String invoke(
		CDataEntity entity,
		String methodName,
		Class<?>[] parameterTypes,
		Object[] arguments)
	{
		try
		{
			Method method = entity.getClass().getMethod(methodName, parameterTypes) ;
			if (method.getDeclaringClass().getName().startsWith("semantic."))
			{
				return null ;
			}
			method.setAccessible(true) ;
			return (String) method.invoke(entity, arguments) ;
		}
		catch (NoSuchMethodException ignored)
		{
			return null ;
		}
		catch (IllegalAccessException | InvocationTargetException error)
		{
			throw new IllegalStateException(
				"Cannot invoke legacy data renderer on "
					+ entity.getClass().getName(),
				error) ;
		}
	}
}
