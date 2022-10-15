package com.awslambdaconvert.aws.deployment.resources;

import com.amazonaws.util.StringUtils;
import com.awslambdaconvert.aws.deployment.config.DeployerConfig;
import com.awslambdaconvert.exception.DeployerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

public class Lambda extends AWSResource {
    private static final Logger LOG = LoggerFactory.getLogger(Lambda.class);

    private final IAM iam;

    public Lambda(IAM iam) {
        this.iam = iam;
    }

    public String deploy(DeployerConfig deployerConfig) {
        LambdaClient awsLambda = LambdaClient.builder()
                .credentialsProvider(credentialsProvider)
                .build();
        String functionArn = null;
        FunctionConfiguration functionConfiguration = getFunction(awsLambda, deployerConfig);
        if (functionConfiguration == null) {
            String roleArn = this.setupLambdaIAM( deployerConfig.getLambdaConfig().getFunctionName());

            int attempts = 0;
            int maxAttempts = 10;
            DeployerException functionCreateException = null;

            // wait until Role available
            while (attempts < maxAttempts) {
                try {
                    LOG.info("Function create attempt #{}", attempts);
                    functionArn = createFunction(awsLambda, deployerConfig, roleArn);
                    if (!StringUtils.isNullOrEmpty(functionArn)) {
                        break;
                    }
                    Thread.sleep(6000);
                } catch (DeployerException e) {
                    functionCreateException = e;
                } catch (InterruptedException e) {
                    functionCreateException = new DeployerException("InterruptedException in crate function", e);
                }
                attempts++;
            }

            if (StringUtils.isNullOrEmpty(functionArn)) {
                throw functionCreateException != null? functionCreateException:
                        new DeployerException("Unable to crate function", new RuntimeException());
            }

        } else {
            functionArn = functionConfiguration.functionArn();
            updateFunctionConfiguration(awsLambda, deployerConfig);
            updateFunctionCode(awsLambda, deployerConfig);
        }
        return functionArn;
    }

    private String createFunction(LambdaClient awsLambda, DeployerConfig deployerConfig, String roleArn) {
        try {
            LOG.info("Lambda function creating");

            DeployerConfig.LambdaConfig lambdaConfig = deployerConfig.getLambdaConfig();
            String functionName = lambdaConfig.getFunctionName();

            LambdaWaiter waiter = awsLambda.waiter();

            FunctionCode code = FunctionCode.builder()
                    .s3Bucket(deployerConfig.getS3Config().getBucketName())
                    .s3Key(deployerConfig.getS3Config().getObjectKey())
                    .build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(functionName)
                    .description(functionName)
                    .code(code)
                    .handler(lambdaConfig.getHandler())
                    .runtime(lambdaConfig.getRuntime())
                    .environment(Environment.builder()
                            .variables(deployerConfig.getLambdaConfig().getEnvironmentVariables()).build())
                    .memorySize(lambdaConfig.getMemorySize())
                    .timeout(lambdaConfig.getTimeout())
                    .role(roleArn)
                    .build();

            // Create a Lambda function using a waiter
            CreateFunctionResponse functionResponse = awsLambda.createFunction(functionRequest);
            GetFunctionRequest getFunctionRequest = GetFunctionRequest.builder()
                    .functionName(functionName)
                    .build();
            WaiterResponse<GetFunctionResponse> waiterResponse = waiter.waitUntilFunctionExists(getFunctionRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);

            // Create function url
            CreateFunctionUrlConfigRequest functionUrlConfigRequest =
                    CreateFunctionUrlConfigRequest.builder().functionName(functionName).authType(FunctionUrlAuthType.NONE).build();
            CreateFunctionUrlConfigResponse createFunctionUrlConfigResponse = awsLambda.createFunctionUrlConfig(functionUrlConfigRequest);

            AddPermissionRequest addPermissionRequest = AddPermissionRequest.builder()
                    .statementId(String.format("%s-func-url-stmt", functionName))
                    .functionName(functionName)
                    .functionUrlAuthType(FunctionUrlAuthType.NONE)
                    .principal("*").action("lambda:InvokeFunctionUrl").build();
            awsLambda.addPermission(addPermissionRequest);

            LOG.info("Lambda function created {}", functionResponse.functionArn());
            LOG.info("Lambda function url {}", createFunctionUrlConfigResponse.functionUrl());

            return functionResponse.functionArn();

        } catch(Exception e) {
            throw new DeployerException("lambda_create_failed", e);
        }
    }

    public static void updateFunctionConfiguration(LambdaClient awsLambda, DeployerConfig deployerConfig ){
        try {
            LOG.info("Lambda function config updating");
            UpdateFunctionConfigurationRequest configurationRequest = UpdateFunctionConfigurationRequest.builder()
                    .functionName(deployerConfig.getLambdaConfig().getFunctionName())
                    .environment(Environment.builder()
                            .variables(deployerConfig.getLambdaConfig().getEnvironmentVariables()).build())
                    .memorySize(deployerConfig.getLambdaConfig().getMemorySize())
                    .timeout(deployerConfig.getLambdaConfig().getTimeout())
                    .build();

            awsLambda.updateFunctionConfiguration(configurationRequest);
            LOG.info("Lambda function config update completed");
        } catch(LambdaException e) {
            LOG.error("Lambda configuration update failed", e);
            throw new DeployerException("lambda_config_update_failed", e);
        }
    }

    public static void updateFunctionCode(LambdaClient awsLambda, DeployerConfig deployerConfig) {
        try {
            LOG.info("Lambda function code updating");
            String functionName = deployerConfig.getLambdaConfig().getFunctionName();
            LambdaWaiter waiter = awsLambda.waiter();
            UpdateFunctionCodeRequest functionCodeRequest = UpdateFunctionCodeRequest.builder()
                    .functionName(functionName)
                    .publish(true)
                    .s3Bucket(deployerConfig.getS3Config().getBucketName())
                    .s3Key(deployerConfig.getS3Config().getObjectKey())
                    .build();

            UpdateFunctionCodeResponse response = awsLambda.updateFunctionCode(functionCodeRequest) ;
            GetFunctionConfigurationRequest getFunctionConfigRequest = GetFunctionConfigurationRequest.builder()
                    .functionName(functionName)
                    .build();

            WaiterResponse<GetFunctionConfigurationResponse> waiterResponse = waiter.waitUntilFunctionUpdated(getFunctionConfigRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            LOG.info("Lambda function code update completed. last modified value {}", response.lastModified());
        } catch(LambdaException e) {
            LOG.error("Lambda function code updat failed", e);
            throw new DeployerException("lambda_code_update_failed", e);
        }
    }

    public static FunctionConfiguration getFunction(LambdaClient awsLambda, DeployerConfig deployerConfig) {
        try {
            GetFunctionRequest functionRequest = GetFunctionRequest.builder()
                    .functionName(deployerConfig.getLambdaConfig().getFunctionName())
                    .build();

            GetFunctionResponse response = awsLambda.getFunction(functionRequest);
            return response.configuration();
        } catch(Exception e) {
            LOG.info("Lambda {} not found", deployerConfig.getLambdaConfig().getFunctionName());
            return null;
        }
    }

    private String setupLambdaIAM(String functionName) {
        String lambdaAssumeRolePolicyDoc = "{\n" +
                "          \"Version\": \"2012-10-17\",\n" +
                "          \"Statement\": [\n" +
                "            {\n" +
                "              \"Action\": \"sts:AssumeRole\",\n" +
                "              \"Principal\": {\n" +
                "                \"Service\": \"lambda.amazonaws.com\"\n" +
                "              },\n" +
                "              \"Effect\": \"Allow\",\n" +
                "              \"Sid\": \"\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }";
        String roleName = String.format("%s-role", functionName);
        String roleArn = iam.createIAMRole(roleName, lambdaAssumeRolePolicyDoc);

        String lambdaDefaultPolicyDoc = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Action\": [\n" +
                "                \"logs:*\"\n" +
                "            ],\n" +
                "            \"Resource\": \"arn:aws:logs:*:*:*\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Action\": [\n" +
                "                \"s3:GetObject\",\n" +
                "                \"s3:PutObject\"\n" +
                "            ],\n" +
                "            \"Resource\": \"arn:aws:s3:::*\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String policyName = String.format("%s-policy", functionName);
        String policyArn = iam.createIAMPolicy(policyName, lambdaDefaultPolicyDoc);

        iam.attachIAMRolePolicy(roleName, policyArn);

        return roleArn;
    }
}
