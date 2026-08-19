package semantic.Verbs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureSection;
import semantic.CProcedureReference;
import utils.CObjectCatalog;

/** Target-neutral semantic model for {@code GO TO A B ... DEPENDING ON value}. */
public class CEntityGotoDepending extends CBaseActionEntity
{
    private final List<CProcedureReference> references =
        new ArrayList<CProcedureReference>();
    private CDataEntity depending;

    /** Creates a new centity goto depending instance. */
    public CEntityGotoDepending(int line, CObjectCatalog catalog, List<String> refs,
        CDataEntity depending, CEntityProcedureSection section)
    {
        super(line, catalog);
        String sectionName = section == null ? "" : section.GetName();
        for (String ref : refs)
        {
            references.add(new CProcedureReference(ref, sectionName, catalog));
        }
        this.depending = depending;
    }

    /** Returns the go to targets. */
    public List<String> getGoToTargets()
    {
        List<String> targets = new ArrayList<String>();
        for (CProcedureReference reference : references)
        {
            CEntityProcedure procedure = reference.getProcedure();
            targets.add(procedure == null ? "[UNDEFINED]" : procedure.GetDisplayName());
        }
        return Collections.unmodifiableList(targets);
    }

    public CDataEntity getDepending()
    {
        return depending;
    }

    @Override
    public void Clear()
    {
        super.Clear();
        for (CProcedureReference reference : references)
        {
            reference.Clear();
        }
        references.clear();
        depending = null;
    }

    @Override
    public boolean ignore()
    {
        return false;
    }
}
