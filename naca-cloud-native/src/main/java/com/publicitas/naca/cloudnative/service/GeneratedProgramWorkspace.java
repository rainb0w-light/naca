package com.publicitas.naca.cloudnative.service;

import java.nio.file.Path;

/** Defines the disposable build workspace used by the transpile-and-run API. */
public final class GeneratedProgramWorkspace {

    private static final String OUTPUT_PROPERTY = "naca.generated.classes.dir";

    private GeneratedProgramWorkspace() {
    }

    /** Executes the classes directory operation. */
    public static Path classesDirectory() {
        String configured = System.getProperty(OUTPUT_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured).toAbsolutePath().normalize();
        }
        return Path.of(System.getProperty("user.dir"), "build", "generated-programs", "classes")
            .toAbsolutePath().normalize();
    }
}
