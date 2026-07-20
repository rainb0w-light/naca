package semantic.Verbs;

import semantic.CBaseLanguageEntity;
import utils.CObjectCatalog;

/** Target-neutral associative tree for ADD's multi-value expression. */
public final class CEntityAddValueTree
{
    private final Object left;
    private final Object right;

    public CEntityAddValueTree(
        Object left,
        Object right,
        CObjectCatalog catalog)
    {
        this.left = left;
        this.right = right;
    }

    public Object getLeft()
    {
        return left;
    }

    public Object getRight()
    {
        return right;
    }

}
