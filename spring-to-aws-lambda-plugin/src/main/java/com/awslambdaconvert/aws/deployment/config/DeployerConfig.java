package com.awslambdaconvert.aws.deployment.config;

import software.amazon.awssdk.services.lambda.model.Runtime;

import java.util.HashMap;
import java.util.Map;

public class DeployerConfig {
    public static class S3Config {
        private final String bucketName;
        private final String objectKey;
        private final String artifactPath;

        public S3Config(String bucketName, String objectKey, String artifactPath) {
            this.bucketName = bucketName;
            this.objectKey = objectKey;
            this.artifactPath = artifactPath;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getArtifactPath() {
            return artifactPath;
        }
    }

    public static class LambdaConfig {
        private final String functionName;

        private Runtime runtime = Runtime.JAVA11;
        private int memorySize = 1024;
        private int timeout = 20;

        private Map<String, String> environmentVariables = new HashMap<>();
        private final String handler = "com.awslambdaconvert.handler.LambdaHandler::handleRequest";

        public LambdaConfig(String functionName, Map<String, String> environmentVariables) {
            this.functionName = functionName;
            this.environmentVariables = environmentVariables;
        }

        public LambdaConfig(String functionName, Map<String, String> environmentVariables, Runtime runtime, int timeout, int memorySize) {
            this.functionName = functionName;
            this.runtime = runtime;
            this.environmentVariables = environmentVariables;
            this.timeout = timeout;
            this.memorySize = memorySize;
        }

        public String getFunctionName() {
            return functionName;
        }

        public Runtime getRuntime() {
            return runtime;
        }

        public Map<String, String> getEnvironmentVariables() {
            return environmentVariables;
        }

        public String getHandler() {
            return handler;
        }

        public int getMemorySize() {
            return memorySize;
        }

        public int getTimeout() {
            return timeout;
        }
    }

    public static class ApiGatewayConfig {
        private final String functionName;

        public ApiGatewayConfig(String functionName) {
            this.functionName = functionName;
        }

        public String getFunctionName() {
            return functionName;
        }
    }


    private final S3Config s3Config;
    private final LambdaConfig lambdaConfig;

    public DeployerConfig(S3Config s3Config, LambdaConfig lambdaConfig) {
        this.s3Config = s3Config;
        this.lambdaConfig = lambdaConfig;
    }

    public S3Config getS3Config() {
        return s3Config;
    }

    public LambdaConfig getLambdaConfig() {
        return lambdaConfig;
    }
}
