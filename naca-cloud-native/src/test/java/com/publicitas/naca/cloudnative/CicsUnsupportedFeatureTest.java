package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * P0 / Decision Log D-001: recognized-but-unlowered syntax must fail closed with
 * a structured, named diagnostic — not silently succeed by dropping the
 * statement. {@code EXEC CICS SEND TEXT} has no semantic lowering today, so its
 * transpilation must fail with an {@code UnsupportedFeatureException} diagnostic
 * (feature id, dialect, source, reason), distinct from
 * {@code MissingTemplateRendererException} (semantic node exists, backend
 * binding missing).
 *
 * <p>Tagged {@code program-root-parity}.
 */
@Tag("program-root-parity")
class CicsUnsupportedFeatureTest
{
    private static final String CICS_SEND_TEXT =
        "       IDENTIFICATION DIVISION.\n"
        + "       PROGRAM-ID. CICS1.\n"
        + "       DATA DIVISION.\n"
        + "       WORKING-STORAGE SECTION.\n"
        + "       01 WS-MSG PIC X(10).\n"
        + "       PROCEDURE DIVISION.\n"
        + "           EXEC CICS SEND TEXT FROM(WS-MSG) END-EXEC.\n"
        + "           STOP RUN.\n";

    @Test
    @DisplayName("EXEC CICS SEND TEXT fails closed with a structured unsupported-feature diagnostic")
    void unsupportedCicsSendTextFailsClosed()
    {
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(CICS_SEND_TEXT, "CICS1");

        assertFalse(result.isSuccess(),
            "unsupported EXEC CICS SEND TEXT must fail closed, not return success");

        String errors = String.join("\n", result.getErrors());
        assertTrue(errors.contains("Unsupported feature: cics.send"),
            "diagnostic must carry the stable feature id; got:\n" + errors);
        assertTrue(errors.contains("dialect: CICS"),
            "diagnostic must carry the dialect; got:\n" + errors);
        assertTrue(errors.contains("syntax recognized but semantic lowering is not implemented"),
            "diagnostic must carry the reason; got:\n" + errors);
    }
}
