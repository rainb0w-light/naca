package semantic.Verbs;

import utils.CObjectCatalog;

/** Target-neutral associative tree for ADD's multi-value expression. */
public final class CEntityAddValueTree
{
    private final Object left;
    private final Object right;

    /** Creates a new centity add value tree instance. */
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
