package generate;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import semantic.CBaseLanguageEntity;

/** Generator-owned output targets for semantic roots whose artifacts are written later. */
public final class LanguageArtifactOutputRegistry
{
    private static final Map<CBaseLanguageEntity, CBaseLanguageExporter> OUTPUTS =
        Collections.synchronizedMap(new WeakHashMap<>()) ;

    private LanguageArtifactOutputRegistry()
    {
    }

    /** Executes the register operation. */
    public static void register(CBaseLanguageEntity entity, CBaseLanguageExporter output)
    {
        if (entity == null)
        {
            return ;
        }
        if (output == null)
        {
            OUTPUTS.remove(entity) ;
        }
        else
        {
            OUTPUTS.put(entity, output) ;
        }
    }

    /** Executes the get operation. */
    public static CBaseLanguageExporter get(CBaseLanguageEntity entity)
    {
        return OUTPUTS.get(entity) ;
    }
}
