package generate.templates.recursive.java;

import generate.templates.recursive.JavaTemplateAssembler;
import org.stringtemplate.v4.ST;
import semantic.expression.CBaseEntityCondition;

final class JavaConditionTemplateSupport
{
    private JavaConditionTemplateSupport()
    {
    }

    static ST child(
        JavaTemplateAssembler assembler,
        int parentPriority,
        CBaseEntityCondition child)
    {
        int childPriority = child.GetPriorityLevel();
        boolean parentheses = (parentPriority == 2 && childPriority == 1)
            || (parentPriority == 1 && childPriority == 2)
            || parentPriority > childPriority;
        return assembler.template("recursiveConditionChild")
            .add("condition", assembler.renderNode(child))
            .add("parentheses", parentheses);
    }
}
