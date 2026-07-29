package generate;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import semantic.expression.CBaseEntityCondExpr;

/**
 * Compatibility boundary for the excluded FPac/BMS direct generators.
 *
 * <p>COBOL semantic expressions are rendered by the recursive assembler. Older
 * non-COBOL generator subclasses can still expose their local {@code Export}
 * method until those separate pipelines are retired; reflection keeps that
 * target protocol out of the semantic hierarchy.
 */
public final class LegacyExpressionRenderer
{
	private LegacyExpressionRenderer()
	{
	}

	public static String render(CBaseEntityCondExpr expression)
	{
		if (expression == null)
		{
			return "[UNDEFINED]" ;
		}
		try
		{
			Method legacyMethod = expression.getClass().getMethod("Export") ;
			if (!legacyMethod.getDeclaringClass().getName().startsWith("semantic."))
			{
				legacyMethod.setAccessible(true) ;
				return (String) legacyMethod.invoke(expression) ;
			}
		}
		catch (NoSuchMethodException ignored)
		{
			// Pure semantic node: use the recursive assembler below.
		}
		catch (IllegalAccessException | InvocationTargetException error)
		{
			throw new IllegalStateException(
				"Cannot render legacy expression " + expression.getClass().getName(),
				error) ;
		}
		return TemplateLoader.getRecursiveAssembler()
			.renderRoot(expression, JavaTemplateRole.REFERENCE) ;
	}
}
