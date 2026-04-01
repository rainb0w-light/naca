package com.publicitas.naca.cloudnative.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.publicitas.naca.cloudnative.model.TranspileRequest;
import com.publicitas.naca.cloudnative.model.TranspileResponse;
import com.publicitas.naca.cloudnative.service.TranspilerService;

@RestController
@RequestMapping("/api")
public class TranspileController {

    private final TranspilerService transpilerService;
    private static final String COBOL_SAMPLES_DIR = "NacaSamples/cobol";
    // Use absolute path for compile output
    private static final String COMPILE_OUTPUT_DIR = System.getProperty("user.dir") + "/NacaSamples/src/";

    public TranspileController(TranspilerService transpilerService) {
        this.transpilerService = transpilerService;
        // Enable auto-compile: transpiled code will be compiled and saved to NacaSamples/src/
        TranspilerService.enableAutoCompile(COMPILE_OUTPUT_DIR);
    }

    /**
     * Transpile COBOL source code to Java.
     * POST /api/transpile
     */
    @PostMapping("/transpile")
    public ResponseEntity<TranspileResponse> transpile(@RequestBody TranspileRequest request) {
        if (request.getCobolSource() == null || request.getCobolSource().isEmpty()) {
            TranspileResponse response = new TranspileResponse();
            response.setSuccess(false);
            response.setErrors(List.of("COBOL source code is required"));
            return ResponseEntity.badRequest().body(response);
        }

        String programName = request.getProgramName();
        if (programName == null || programName.isEmpty()) {
            programName = "PROGRAM";
        }

        TranspilerService.TranspileResult result = transpilerService.transpile(
            request.getCobolSource(), programName);

        TranspileResponse response = new TranspileResponse();
        response.setSuccess(result.isSuccess());
        response.setJavaSource(result.getJavaSource());
        response.setErrors(result.getErrors());

        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Transpile COBOL file to Java.
     * POST /api/transpile/file
     */
    @PostMapping("/transpile/file")
    public ResponseEntity<TranspileResponse> transpileFile(MultipartFile file) {
        if (file.isEmpty()) {
            TranspileResponse response = new TranspileResponse();
            response.setSuccess(false);
            response.setErrors(List.of("File is required"));
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String cobolSource = new String(file.getBytes());
            String programName = file.getOriginalFilename();
            if (programName != null && programName.contains(".")) {
                programName = programName.substring(0, programName.lastIndexOf('.'));
            }

            TranspilerService.TranspileResult result = transpilerService.transpile(
                cobolSource, programName != null ? programName : "PROGRAM");

            TranspileResponse response = new TranspileResponse();
            response.setSuccess(result.isSuccess());
            response.setJavaSource(result.getJavaSource());
            response.setErrors(result.getErrors());

            if (result.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IOException e) {
            TranspileResponse response = new TranspileResponse();
            response.setSuccess(false);
            response.setErrors(List.of("Failed to read file: " + e.getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * List available sample COBOL programs.
     * GET /api/samples
     */
    @GetMapping("/samples")
    public ResponseEntity<List<String>> listSamples() {
        List<String> samples = new ArrayList<>();

        // Look for COBOL files in NacaSamples/cobol directory
        Path samplesPath = Paths.get(COBOL_SAMPLES_DIR);
        if (Files.exists(samplesPath)) {
            try (Stream<Path> paths = Files.walk(samplesPath)) {
                samples = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".cbl") || p.toString().endsWith(".cob"))
                    .map(p -> {
                        String name = p.getFileName().toString();
                        if (name.contains(".")) {
                            return name.substring(0, name.lastIndexOf('.'));
                        }
                        return name;
                    })
                    .collect(Collectors.toList());
            } catch (IOException e) {
                // Ignore and return empty list
            }
        }

        // Fallback to known sample names
        if (samples.isEmpty()) {
            samples.add("BATCH1");
            samples.add("CALLMSG");
            samples.add("ONLINE1");
        }

        return ResponseEntity.ok(samples);
    }
}
