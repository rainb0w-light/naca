package com.publicitas.naca.tests.acceptance;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import idea.onlinePrgEnv.OnlineSession;
import jlib.misc.LogicalFileDescriptor;
import org.junit.jupiter.api.Test;

/** Regression coverage for acceptance logical-file descriptor aliases. */
class AcceptanceProgramRunnerTest {

    @Test
    void registersBusinessAndGeneratedLogicalNames() {
        assertDoesNotThrow(this::assertBusinessAndGeneratedMappings,
            "named descriptors should register both runtime lookup keys");
    }

    private void assertBusinessAndGeneratedMappings() {
        OnlineSession session = new OnlineSession(false);
        LogicalFileDescriptor mapped = AcceptanceProgramRunner.registerNamedDescriptor(
            session, "CUSTFILE", "/tmp/customer.dat", "ascii,fb,500");

        assertSame(mapped, session.getLogicalFileDescriptor("CUSTFILE"),
            "business logical name should resolve the mapped descriptor");
        assertSame(mapped, session.getLogicalFileDescriptor("CUSTFILE-FILE"),
            "generated logical name should resolve the same descriptor instance");
    }

    @Test
    void doesNotDoubleSuffixGeneratedLogicalName() {
        assertDoesNotThrow(this::assertAlreadySuffixedMapping,
            "an already generated logical name should not be suffixed twice");
    }

    private void assertAlreadySuffixedMapping() {
        OnlineSession session = new OnlineSession(false);
        LogicalFileDescriptor mapped = AcceptanceProgramRunner.registerNamedDescriptor(
            session, "ARCHIVE-FILE", "/tmp/archive.dat", "ascii,fb,80");

        assertSame(mapped, session.getLogicalFileDescriptor("ARCHIVE-FILE"),
            "already suffixed logical name should retain its descriptor");
        assertNull(session.getLogicalFileDescriptor("ARCHIVE-FILE-FILE"),
            "registration must not create a double-suffixed alias");
    }
}
