package com.publicitas.naca.cloudnative;

import nacaLib.basePrgEnv.BaseCESMManager;
import nacaLib.varEx.Var;
import org.junit.jupiter.api.Test;

class CICSDeleteQRuntimeShapeTest
{
    /**
     * Compile-time proof for every Java expression emitted by
     * recursiveCICSDeleteQEntity. The method need not execute: compilation proves
     * both queue variants return the fluent type that owns sysID(Var).
     */
    @Test
    void everyGeneratedDeleteQShapeCompiles()
    {
        generatedShapes(null, null, null);
    }

    private static void generatedShapes(BaseCESMManager cesm, Var queue, Var sysID)
    {
        if (cesm == null)
        {
            return;
        }
        cesm.deleteTempQueue(queue);
        cesm.deleteTempQueue(queue).sysID(sysID);
        cesm.deleteTransiantQueue(queue);
        cesm.deleteTransiantQueue(queue).sysID(sysID);
    }
}
