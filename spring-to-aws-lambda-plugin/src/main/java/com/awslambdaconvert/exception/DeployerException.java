package com.awslambdaconvert.exception;

public class DeployerException extends RuntimeException {
    public DeployerException(String message, Exception e) {
        super(message, e);
    }
}
