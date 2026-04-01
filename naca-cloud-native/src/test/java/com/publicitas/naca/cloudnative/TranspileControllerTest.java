package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.publicitas.naca.cloudnative.model.TranspileRequest;
import com.publicitas.naca.cloudnative.model.TranspileResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TranspileControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testTranspileEmptySource() {
        TranspileRequest request = new TranspileRequest();
        request.setCobolSource("");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<TranspileRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TranspileResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/transpile", entity, TranspileResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testTranspileNullSource() {
        TranspileRequest request = new TranspileRequest();
        request.setCobolSource(null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<TranspileRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TranspileResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/transpile", entity, TranspileResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testTranspileInvalidCobol() {
        TranspileRequest request = new TranspileRequest();
        request.setCobolSource("This is not valid COBOL");
        request.setProgramName("INVALID");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<TranspileRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TranspileResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/transpile", entity, TranspileResponse.class);

        // Should fail because missing IDENTIFICATION DIVISION
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testTranspileValidCobolWithWorkingStorage() {
        // COBOL source with WORKING-STORAGE variables
        String cobolSource = "       IDENTIFICATION DIVISION.\n" +
                            "       PROGRAM-ID. TESTPROG.\n" +
                            "       DATA DIVISION.\n" +
                            "       WORKING-STORAGE SECTION.\n" +
                            "       01 WS-MESSAGE.\n" +
                            "          05 WS-GREETING    PIC X(20) VALUE 'Hello'.\n" +
                            "          05 WS-STATUS      PIC X(10) VALUE 'SUCCESS'.\n" +
                            "       01 WS-COUNTER         PIC S9(5) COMP-3 VALUE ZERO.\n" +
                            "       PROCEDURE DIVISION.\n" +
                            "       MAIN-PROCEDURE.\n" +
                            "           DISPLAY 'Hello'.\n" +
                            "           STOP RUN.";

        TranspileRequest request = new TranspileRequest();
        request.setCobolSource(cobolSource);
        request.setProgramName("TESTPROG");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<TranspileRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TranspileResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/transpile", entity, TranspileResponse.class);

        // Should succeed with valid COBOL
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        String javaSource = response.getBody().getJavaSource();
        assertNotNull(javaSource);

        // Verify WORKING-STORAGE variables are present in generated code (converted to Java style)
        assertTrue(javaSource.contains("wsMessage"), "Should contain wsMessage variable");
        assertTrue(javaSource.contains("wsGreeting"), "Should contain wsGreeting variable");
        assertTrue(javaSource.contains("wsStatus"), "Should contain wsStatus variable");
        assertTrue(javaSource.contains("wsCounter"), "Should contain wsCounter variable");
        assertTrue(javaSource.contains("declare.level("), "Should use NacaTrans declare pattern");

        // Verify procedure division contains translated statements (not just comments)
        assertTrue(javaSource.contains("display("), "Should contain display() method call");
        assertTrue(javaSource.contains("return;"), "Should contain return statement for STOP RUN");
    }

    @Test
    void testListSamples() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/samples", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Response should be a JSON array
        assertTrue(response.getBody().startsWith("["));
    }
}
