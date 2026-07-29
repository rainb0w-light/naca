package generate.java.st;

import semantic.CDataEntity;
import utils.CObjectCatalog;
import generate.CBaseLanguageExporter;

/**
 * Mock data entity for testing ST4 templates.
 * Provides configurable test values for template rendering.
 */
public class MockDataEntity extends CDataEntity {
    private String mockReferenceValue;

    public MockDataEntity(int line, String referenceValue) {
        super(line, "", null);
        this.mockReferenceValue = referenceValue;
    }

    public MockDataEntity(int line, CObjectCatalog cat, CBaseLanguageExporter out, String referenceValue) {
        super(line, "", cat);
        setLanguageExporter(out);
        this.mockReferenceValue = referenceValue;
    }

        public String ExportReference(int nLine) {
        return mockReferenceValue;
    }

    @Override
    protected void RegisterMySelfToCatalog() {
        // No-op for mock
    }

    @Override
    public boolean ignore() {
        return false;
    }

    @Override
    public String GetConstantValue() {
        return mockReferenceValue;
    }

    @Override
    protected void DoExport() {
        WriteLine(mockReferenceValue);
    }

    @Override
    public boolean isValNeeded() {
        return false;
    }

        public String ExportWriteAccessorTo(String varName) {
        return mockReferenceValue + " = " + varName + ";";
    }

    @Override
    public boolean HasAccessors() {
        return false;
    }

    @Override
    public CDataEntityType GetDataType() {
        return CDataEntityType.CONSTANT;
    }

    // Setters for test configuration
    public void setMockReferenceValue(String value) {
        this.mockReferenceValue = value;
    }

    /**
     * Bean-style accessor used by the recursive ST binding
     * (generate.java.st.MockDataEntity=mockDataEntityReference) so the mock
     * renders its configured reference value through the assembler.
     */
    public String getMockReferenceValue() {
        return mockReferenceValue;
    }
}
