package com.awslambdaconvert.aws.deployment.resources;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

public class AWSResource {
    protected final AwsCredentialsProvider credentialsProvider;

    public AWSResource() {
        this.credentialsProvider = DefaultCredentialsProvider
                .create();
    }
}
