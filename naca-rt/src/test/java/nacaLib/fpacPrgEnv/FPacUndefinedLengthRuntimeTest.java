package nacaLib.fpacPrgEnv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jlib.misc.BasePic9Comp3BufferSupport;
import nacaLib.tempCache.TempCacheLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Verifies numeric operations on FPac references whose source length is inferred at runtime. */
class FPacUndefinedLengthRuntimeTest
{
    @BeforeEach
    void initializeRuntimeCache()
    {
        BasePic9Comp3BufferSupport.init();
        TempCacheLocator.setTempCache();
    }

    @AfterEach
    void releaseRuntimeCache()
    {
        TempCacheLocator.relaseTempCache();
    }

    private static final class TestProgram extends FPacProgram
    {
        private VarFPacLengthUndef numericReference()
        {
            return workingP(6000);
        }

        private void setValue(int value)
        {
            move(value, numericReference());
        }

        private int value()
        {
            return numericReference().getInt();
        }

        private boolean greaterThan(int value)
        {
            return isGreater(numericReference(), value);
        }

        private void increment(int amount)
        {
            inc(amount, numericReference());
        }

        @Override
        protected int first()
        {
            return NEXT;
        }

        @Override
        protected int normal()
        {
            return NEXT;
        }

        @Override
        protected int last()
        {
            return END;
        }
    }

    @Test
    void readsComparesAndIncrementsTheInferredNumericVariable()
    {
        TestProgram program = new TestProgram();
        program.setValue(10);

        assertEquals(10, program.value());
        assertTrue(program.greaterThan(9));
        assertFalse(program.greaterThan(10));

        program.increment(5);
        assertEquals(15, program.value());
    }
}
