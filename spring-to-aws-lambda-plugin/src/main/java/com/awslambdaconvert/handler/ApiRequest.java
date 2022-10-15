package com.awslambdaconvert.handler;

public class ApiRequest {
    private final String path;
    private final String httpMethod;

    public ApiRequest(String path, String httpMethod) {
        this.path = path;
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}
