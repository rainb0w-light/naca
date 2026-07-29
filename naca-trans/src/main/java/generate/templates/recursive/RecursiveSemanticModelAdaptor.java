package generate.templates.recursive;

import generate.templates.recursive.java.JavaIdentifierValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityExternalDataStructure;
import semantic.CEntityInline;
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
        boolean artifactContext = assembler.isArtifactContext(self);
        if (artifactContext)
        {
            value = adaptIdentifier(model, propertyName, value);
        }
        return adapt(
            value, assembler.childRole(self, propertyName), artifactContext);
    }

    private Object adapt(
        Object value, JavaTemplateRole role, boolean artifactContext)
    {
        if (value instanceof CEntityAddValueTree)
        {
            return assembler.renderNode(value, role, artifactContext);
        }
        if (value instanceof CBaseLanguageEntity)
        {
            return assembler.renderNode(value, role, artifactContext);
        }
        if (value instanceof Collection<?> values)
        {
            List<Object> adapted = new ArrayList<>(values.size());
            for (Object item : values)
            {
                adapted.add(adapt(item, role, artifactContext));
            }
            return Collections.unmodifiableList(adapted);
        }
        return value;
    }

    private Object adaptIdentifier(
        Object model, String propertyName, Object value)
    {
        if ("formattedName".equals(propertyName)
            && model instanceof CBaseLanguageEntity entity
            && usesLegacyCopybookIdentifier(entity))
        {
            return new JavaIdentifierValue(entity.GetDisplayName());
        }
        if ("qualifierFormattedName".equals(propertyName)
            && model instanceof CDataEntity data
            && data.getQualifierFormattedName() != null)
        {
            return new JavaIdentifierValue(data.getQualifierFormattedName());
        }
        return value;
    }

    private boolean usesLegacyCopybookIdentifier(CBaseLanguageEntity entity)
    {
        if (entity instanceof CEntityInline
            || entity instanceof CEntityExternalDataStructure)
        {
            return true;
        }
        if (entity instanceof CDataEntity data
            && data.getQualifierFormattedName() != null)
        {
            return true;
        }
        CBaseLanguageEntity ancestor = entity.GetParent();
        while (ancestor != null)
        {
            if (ancestor instanceof CEntityExternalDataStructure)
            {
                return true;
            }
            ancestor = ancestor.GetParent();
        }
        return false;
    }
}
