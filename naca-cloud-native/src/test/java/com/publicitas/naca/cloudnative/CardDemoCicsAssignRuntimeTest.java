package com.publicitas.naca.cloudnative;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.misc.CCESMFakeMethodContainer;
import nacaLib.varEx.Var;
import org.junit.jupiter.api.Test;

/** Runtime proof for cloud-provided EXEC CICS ASSIGN identifiers. */
class CardDemoCicsAssignRuntimeTest
{
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void assignsApplicationAndSystemIdentifiersFromTheExecutionEnvironment()
    {
        BaseEnvironment environment = mock(BaseEnvironment.class);
        Var applicationId = mock(Var.class);
        Var systemId = mock(Var.class);
        when(environment.getConfigOption("APPLID")).thenReturn("CARDDEMO");
        when(environment.getConfigOption("SYSID")).thenReturn("NACA");

        CCESMFakeMethodContainer assign = new CCESMFakeMethodContainer(environment);
        assign.APPLID(applicationId).sysID(systemId);

        verify(applicationId).set("CARDDEMO");
        verify(systemId).set("NACA");
    }
}
