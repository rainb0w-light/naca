package com.publicitas.naca.cloudnative.model;

public class RunRequest {
    private String programName;
    private String programType; // "batch" or "online"

    public RunRequest() {
    }

    public RunRequest(String programName, String programType) {
        this.programName = programName;
        this.programType = programType;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }
}
