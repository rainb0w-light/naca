package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Recognized CICS commands outside the CardDemo translation slice must remain
 * fail-closed instead of disappearing from generated Java.
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class CicsUnsupportedFeatureTest
{
    private static final String CICS_SEND_CONTROL =
        "       IDENTIFICATION DIVISION.\n"
        + "       PROGRAM-ID. CICS1.\n"
        + "       DATA DIVISION.\n"
        + "       PROCEDURE DIVISION.\n"
        + "           EXEC CICS SEND CONTROL ERASE END-EXEC.\n"
        + "           STOP RUN.\n";

    @Test
    @DisplayName("EXEC CICS SEND CONTROL remains fail-closed")
    void unsupportedCicsSendControlFailsClosed()
    {
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(CICS_SEND_CONTROL, "CICS1");

        assertFalse(result.isSuccess());
        String errors = String.join("\n", result.getErrors());
        assertTrue(errors.contains("Unsupported feature: cics.send.control"), errors);
    }
}
