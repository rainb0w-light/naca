package com.publicitas.naca.cloudnative.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.publicitas.naca.cloudnative.model.RunRequest;
import com.publicitas.naca.cloudnative.model.RunResponse;
import com.publicitas.naca.cloudnative.service.RunnerService;

@RestController
@RequestMapping("/api")
public class RunController {

    private final RunnerService runnerService;

    public RunController(RunnerService runnerService) {
        this.runnerService = runnerService;
    }

    /**
     * Run a transpiled COBOL program.
     * POST /api/run
     */
    @PostMapping("/run")
    public ResponseEntity<RunResponse> runProgram(@RequestBody RunRequest request) {
        if (request.getProgramName() == null || request.getProgramName().isEmpty()) {
            RunResponse response = new RunResponse();
            response.setSuccess(false);
            response.setErrors(List.of("Program name is required"));
            return ResponseEntity.badRequest().body(response);
        }

        String programType = request.getProgramType();
        if (programType == null || programType.isEmpty()) {
            programType = "batch"; // Default to batch
        }

        RunnerService.RunResult result = runnerService.runProgram(
            request.getProgramName(), programType);

        RunResponse response = new RunResponse();
        response.setSuccess(result.isSuccess());
        response.setOutput(result.getOutput());
        response.setErrors(result.getErrors());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
