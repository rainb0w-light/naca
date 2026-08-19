package generate.java.st;

import semantic.CEntityBloc;
import utils.CObjectCatalog;
import semantic.CBaseLanguageEntity;

/**
 * Mock bloc for testing ST4 templates.
 * Contains configurable child statements for template rendering.
 */
public class MockBloc extends CEntityBloc {
    /** Creates a new mock bloc instance. */
    public MockBloc(int line) {
        super(line, null);
    }

    @Override
    protected void RegisterMySelfToCatalog() {
        // No-op for mock
    }

    @Override
    public boolean ignore() {
        return false;
    }
    protected void DoExport() {
        // No-op for mock
    }

    /**
     * Add a child entity to this mock bloc.
     * @param child The child entity to add
     */
    public void addChild(CBaseLanguageEntity child) {
        AddChild(child);
    }
}
