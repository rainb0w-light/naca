package nacaLib.testSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TestAssertionCollectorTest {

    @AfterEach
    void stopCollector() {
        TestAssertionCollector.stopCollecting();
        TestAssertionCollector.clear();
    }

    @Test
    void recordsDifferentValuesAsSuccessfulForNegativeEqualityAssertions() {
        TestAssertionCollector.startCollecting();

        TestAssertionCollector.assertNotEquals("actual", "prohibited", "must differ");

        assertFalse(TestAssertionCollector.getInstance().hasFailures());
    }

    @Test
    void recordsEqualValuesAsFailuresForNegativeEqualityAssertions() {
        TestAssertionCollector.startCollecting();

        TestAssertionCollector.assertNotEquals("same", "same", "must differ");

        TestAssertionCollector collector = TestAssertionCollector.getInstance();
        assertTrue(collector.hasFailures());
        assertEquals(1, collector.getFailureCount());
    }
}
