package semantic;

import semantic.Verbs.CEntityConvertReference;
import utils.CObjectCatalog;

/**
 * Target-neutral FPac positioned-buffer reference. Unlike a COBOL substring,
 * it addresses the byte buffer behind a file descriptor or accessor.
 */
public class CPositionedBufferReference extends CSubStringAttributReference
{
    public CPositionedBufferReference(int line, CObjectCatalog catalog)
    {
        super(line, catalog);
    }

    @Override
    public CDataEntityType GetDataType()
    {
        return reference == null ? CDataEntityType.UNKNWON : reference.GetDataType();
    }

    @Override
    public boolean HasAccessors()
    {
        return false;
    }

    @Override
    public boolean isValNeeded()
    {
        return false;
    }

    public boolean isWrappedReferenceAccessor()
    {
        return reference != null && reference.HasAccessors();
    }

    public boolean isOpenConversionCall()
    {
        if (!(reference instanceof CEntityConvertReference conversion))
        {
            return false;
        }
        return !conversion.isPlainReference() && !conversion.isAccessorConversion();
    }
}
