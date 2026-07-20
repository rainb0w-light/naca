package generate.templates.recursive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityAddValueTree;

/** Recursively turns semantic child properties into nested, unrendered ST instances. */
final class RecursiveSemanticModelAdaptor extends ObjectModelAdaptor<Object>
{
    private final JavaTemplateAssembler assembler;

    RecursiveSemanticModelAdaptor(JavaTemplateAssembler assembler)
    {
        this.assembler = assembler;
    }

    @Override
    public Object getProperty(
        Interpreter interpreter,
        ST self,
        Object model,
        Object property,
        String propertyName) throws STNoSuchPropertyException
    {
        return adapt(super.getProperty(interpreter, self, model, property, propertyName));
    }

    private Object adapt(Object value)
    {
        if (value instanceof CEntityAddValueTree)
        {
            return assembler.renderNode(value);
        }
        if (value instanceof CBaseLanguageEntity)
        {
            return assembler.renderNode(value);
        }
        if (value instanceof Collection<?> values)
        {
            List<Object> adapted = new ArrayList<>(values.size());
            for (Object item : values)
            {
                adapted.add(adapt(item));
            }
            return Collections.unmodifiableList(adapted);
        }
        return value;
    }
}
