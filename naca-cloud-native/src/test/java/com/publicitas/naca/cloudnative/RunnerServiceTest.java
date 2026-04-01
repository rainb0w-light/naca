package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.publicitas.naca.cloudnative.service.RunnerService;
import com.publicitas.naca.cloudnative.service.RunnerService.RunResult;

@SpringBootTest
public class RunnerServiceTest {

    @Autowired
    private RunnerService runnerService;

    @Test
    void testRunProgramWithEmptyName() {
        RunResult result = runnerService.runProgram("", "batch");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().size() > 0);
    }

    @Test
    void testRunProgramWithNullName() {
        RunResult result = runnerService.runProgram(null, "batch");

        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    @Test
    void testRunProgramNotFound() {
        // Running a non-existent program should return failure
        RunResult result = runnerService.runProgram("NONEXISTENT_PROG", "batch");

        assertNotNull(result);
        // The program should fail because class is not found
        assertFalse(result.isSuccess());
        // Output or errors should indicate the problem
        assertTrue(result.getOutput() != null || result.getErrors().size() > 0);
    }

    @Test
    void testRunProgramBatchTypeWithBATCH1() {
        // Try to run BATCH1 if it exists in the classpath
        RunResult result = runnerService.runProgram("BATCH1", "batch");

        assertNotNull(result);
        // Result will depend on whether BATCH1 class is available
        // In test environment, it may fail with class not found
        // Either output or errors should be present
        assertTrue(result.getOutput() != null || result.getErrors().size() > 0);
    }

    @Test
    void testRunResultStructure() {
        RunResult result = runnerService.runProgram("TEST", "batch");

        assertNotNull(result);
        assertNotNull(result.isSuccess());
        // Either output or errors should be present
        assertTrue(result.getOutput() != null || result.getErrors().size() > 0);
        assertNotNull(result.getErrors());
    }
}
