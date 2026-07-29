package generate;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import semantic.CBaseLanguageEntity;

/**
 * Compatibility registry for excluded direct-generation pipelines.
 *
 * <p>Output controllers belong to the generator layer, so semantic entities no
 * longer expose them. Weak keys prevent this temporary bridge from extending a
 * semantic tree's lifetime.
 */
public final class LegacyLanguageOutputRegistry
{
	private static final Map<CBaseLanguageEntity, CBaseLanguageExporter> OUTPUTS =
		Collections.synchronizedMap(new WeakHashMap<>()) ;

	private LegacyLanguageOutputRegistry()
	{
	}

	public static void register(
		CBaseLanguageEntity entity,
		CBaseLanguageExporter output)
	{
		if (entity != null)
		{
			if (output == null)
			{
				OUTPUTS.remove(entity) ;
			}
			else
			{
				OUTPUTS.put(entity, output) ;
			}
		}
	}

	public static CBaseLanguageExporter get(CBaseLanguageEntity entity)
	{
		return OUTPUTS.get(entity) ;
	}
}
