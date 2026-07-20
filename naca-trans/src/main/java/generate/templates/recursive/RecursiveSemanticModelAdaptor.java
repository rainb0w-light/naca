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
        Object value = super.getProperty(interpreter, self, model, property, propertyName);
        return adapt(value, assembler.childRole(self, propertyName));
    }

    private Object adapt(Object value, JavaTemplateRole role)
    {
        if (value instanceof CEntityAddValueTree)
        {
            return assembler.renderNode(value, role);
        }
        if (value instanceof CBaseLanguageEntity)
        {
            return assembler.renderNode(value, role);
        }
        if (value instanceof Collection<?> values)
        {
            List<Object> adapted = new ArrayList<>(values.size());
            for (Object item : values)
            {
                adapted.add(adapt(item, role));
            }
            return Collections.unmodifiableList(adapted);
        }
        return value;
    }
}
