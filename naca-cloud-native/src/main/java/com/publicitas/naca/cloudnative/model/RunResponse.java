package com.publicitas.naca.cloudnative.model;

import java.util.List;

public class RunResponse {
    private boolean success;
    private String output;
    private List<String> errors;

    public RunResponse() {
    }

    public RunResponse(boolean success, String output, List<String> errors) {
        this.success = success;
        this.output = output;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
