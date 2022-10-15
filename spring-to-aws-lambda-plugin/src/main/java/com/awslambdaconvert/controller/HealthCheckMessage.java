package com.awslambdaconvert.controller;

public class HealthCheckMessage {
    private final boolean status;

    public HealthCheckMessage(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
