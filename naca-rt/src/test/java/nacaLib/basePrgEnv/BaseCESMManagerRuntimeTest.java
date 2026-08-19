package nacaLib.basePrgEnv;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nacaLib.program.CJMapRunnable;
import org.junit.jupiter.api.Test;

class BaseCESMManagerRuntimeTest
{
    private final BaseCESMManager manager = new BaseCESMManager(null);

    @Test
    void enqAndDeqUseAReentrantResourceLock()
    {
        manager.enQ("ACCOUNT:42");
        manager.enQ("ACCOUNT:42");
        manager.deQ("ACCOUNT:42");
        manager.deQ("ACCOUNT:42");

        assertThrows(IllegalStateException.class, () -> manager.deQ("ACCOUNT:42"));
    }

    @Test
    void handleAidRegistersAndRemovesNormalizedConditions()
    {
        CJMapRunnable target = new CJMapRunnable()
        {
            @Override
            public void run()
            {
            }
        };

        manager.handleAID(" enter ", target);
        assertSame(target, manager.getAIDHandler("ENTER"));

        manager.unhandleAID("Enter");
        assertNull(manager.getAIDHandler("ENTER"));
    }

    @Test
    void runtimeEntryPointsWithoutABackendFailClosed()
    {
        assertThrows(UnsupportedOperationException.class, manager::getMain);
        assertThrows(UnsupportedOperationException.class, manager::inquire);
        assertThrows(UnsupportedOperationException.class,
            () -> manager.readFile("CUSTOMER"));
        assertThrows(UnsupportedOperationException.class,
            () -> manager.writeDataSet("CUSTOMER"));
    }

    @Test
    void compatibilityBuilderRequiresAnEnvironmentForSysidAssignment()
    {
        assertThrows(IllegalStateException.class,
            () -> new nacaLib.misc.CCESMFakeMethodContainer().sysID(null));
    }
}
