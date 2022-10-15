package com.awslambdaconvert.aws.deployment;

import com.awslambdaconvert.aws.deployment.config.DeployerConfig;
import com.awslambdaconvert.aws.deployment.resources.IAM;
import com.awslambdaconvert.aws.deployment.resources.Lambda;
import com.awslambdaconvert.aws.deployment.resources.S3;

public class Deployer {
    private final IAM iam;
    private final S3 s3;
    private final Lambda lambda;

    public Deployer() {
        this.iam = new IAM();
        this.s3 = new S3();
        this.lambda = new Lambda(this.iam);
    }

    public void deploy(DeployerConfig deployerConfig) {
        this.s3.createBucket(deployerConfig.getS3Config());
        this.s3.uploadArtifact(deployerConfig.getS3Config());
        this.lambda.deploy(deployerConfig);
    }
}
