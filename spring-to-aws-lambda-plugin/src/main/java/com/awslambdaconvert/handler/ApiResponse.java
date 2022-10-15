package com.awslambdaconvert.handler;

public class ApiResponse {
    private final Object message;
    private final int statueCode;

    public ApiResponse(Object message, int statueCode) {
        this.message = message;
        this.statueCode = statueCode;
    }

    public Object getMessage() {
        return message;
    }

    public int getStatueCode() {
        return statueCode;
    }
}
