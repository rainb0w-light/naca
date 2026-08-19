package semantic.expression;

import semantic.CDataEntity;

/**
 * Semantic representation of a configured Java-side symbolic constant.
 * The value is already a code symbol (for example {@code LanguageCode.EN})
 * and is formatted only by the ST4 backend.
 */
public class CEntityConstantValue extends CDataEntity
{
    /** Creates a new centity constant value instance. */
    public CEntityConstantValue(String value)
    {
        super(0, "", null);
        this.value = value;
    }

    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.CONSTANT;
    }

    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }

    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return value;
    }

    public String getLiteralValue()
    {
        return value;
    }

    public boolean isValNeeded()
    {
        return false;
    }

    private final String value;
}
