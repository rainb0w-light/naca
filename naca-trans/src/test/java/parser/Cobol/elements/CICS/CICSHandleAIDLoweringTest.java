package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.st.MockJavaExporter;
import org.junit.jupiter.api.Test;
import parser.CIdentifier;
import semantic.CICS.CEntityCICSHandleAID;

class CICSHandleAIDLoweringTest
{
    @Test
    void lowersTheAidCollectionRatherThanTheConditionCollection()
    {
        TestableHandle parserNode = new TestableHandle();
        parserNode.addAID("ENTER", new CIdentifier("ENTER-KEY"));
        parserNode.addAID("ANYKEY", null);

        CEntityCICSHandleAID entity = new CEntityCICSHandleAID(1, null);
        entity.setLanguageExporter(new MockJavaExporter());
        parserNode.lowerInto(entity);

        assertEquals(1, entity.getHandledAIDEntries().size());
        assertEquals("ENTER", entity.getHandledAIDEntries().get(0).getCondition());
        // Lowering preserves the COBOL label; Java identifier formatting belongs
        // to the recursive ST4 assembler.
        assertEquals("ENTER-KEY", entity.getHandledAIDEntries().get(0).getLabel());
        assertEquals(1, entity.getUnhandledAIDEntries().size());
        assertEquals("ANYKEY", entity.getUnhandledAIDEntries().get(0).getCondition());
    }

    private static final class TestableHandle extends CExecCICSHandle
    {
        private TestableHandle()
        {
            super(1);
        }

        private void addAID(String condition, CIdentifier label)
        {
            aID.add(condition);
            labels.add(label);
        }

        private void lowerInto(CEntityCICSHandleAID entity)
        {
            PopulateAIDEntity(entity);
        }
    }
}
