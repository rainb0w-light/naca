package generate.java.st;

import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;
import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;

/**
 * Mock condition for testing ST4 templates.
 * Provides configurable condition expressions for template rendering.
 */
public class MockCondition extends CBaseEntityCondition {
    private String mockConditionValue;
    private boolean mockIgnoreValue = false;

    /** Creates a new mock condition instance. */
    public MockCondition(int line, String conditionValue) {
        super();
        this.mockConditionValue = conditionValue;
    }

    /** Creates a new mock condition instance. */
    public MockCondition(int line, CObjectCatalog cat, CBaseLanguageExporter out, String conditionValue) {
        super();
        this.mockConditionValue = conditionValue;
    }

    /** Executes the export operation. */
    public String Export() {
        return mockConditionValue;
    }

    public String getCodeString() {
        return mockConditionValue;
    }

    @Override
    public boolean ignore() {
        return mockIgnoreValue;
    }

    @Override
    protected void RegisterMySelfToCatalog() {
        // No-op for mock
    }

    // Required abstract method implementations
    @Override
    public int GetPriorityLevel() {
        return 0;
    }

    @Override
    public CBaseEntityCondition GetOppositeCondition() {
        return this; // Simplified for testing
    }

    @Override
    public boolean isBinaryCondition() {
        return false;
    }

    @Override
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace) {
        return this; // Simplified for testing
    }

    @Override
    public CDataEntity GetConditionReference() {
        return null; // Simplified for testing
    }

    @Override
    public void SetConditonReference(CDataEntity e) {
        // No-op for testing
    }

    // Setters for test configuration
    public void setMockConditionValue(String value) {
        this.mockConditionValue = value;
    }

    public void setMockIgnoreValue(boolean ignore) {
        this.mockIgnoreValue = ignore;
    }
}
