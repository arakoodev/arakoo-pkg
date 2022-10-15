package com.awslambdaconvert.aws.handler;

import com.awslambdaconvert.aws.deployment.Deployer;
import com.awslambdaconvert.aws.deployment.config.DeployerConfig;
import com.awslambdaconvert.aws.deployment.resources.IAM;
import com.awslambdaconvert.aws.deployment.resources.Lambda;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeployerTest {
    @Test
    void test() {
        String buildPath = "/Users/tharangathennakoon/Documents/my/fiverr/spring-to-lambda/spring-to-aws-lambda/build";
        String bucketName = "spring-to-aws-lambda";
        String objectKey = "spring-to-aws-lambda-0.0.1-SNAPSHOT.zip";
        String artifactPath = String.format("%s%s%s%s%s",
                buildPath, File.separator, "distributions", File.separator, objectKey);
        String functionName = String.format("%s-%s", "spring-to-aws-lambda", "0.0.1-SNAPSHOT").toLowerCase();
        Deployer deployer = new Deployer();
        deployer.deploy(
                new DeployerConfig(
                        new DeployerConfig.S3Config(
                                bucketName, objectKey, artifactPath
                        ), new DeployerConfig.LambdaConfig(functionName, new HashMap<>())));
    }

    @Test
    void lambdaDeployTest() {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("APPLICATION_START_CLASS", "com.awslambdaconvert.service.order.Application");

        String bucketName = "spring-to-aws-lambda";
        String objectKey = "spring-to-aws-lambda-0.0.1-SNAPSHOT.zip";
        String functionName = String.format("%s", "test-spring-to-aws-lambda").toLowerCase();

        IAM iam = new IAM();

        Lambda deployer = new Lambda(iam);
        deployer.deploy(new DeployerConfig(
                new DeployerConfig.S3Config(
                        bucketName, objectKey, "NO_NEED_FOR_THIS"
                ), new DeployerConfig.LambdaConfig(functionName, environmentVariables)));
    }
}
