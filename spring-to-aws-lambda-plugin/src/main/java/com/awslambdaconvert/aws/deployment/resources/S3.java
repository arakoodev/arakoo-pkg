package com.awslambdaconvert.aws.deployment.resources;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.awslambdaconvert.exception.DeployerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.awslambdaconvert.aws.deployment.config.DeployerConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class S3 extends AWSResource {
    private static final Logger LOG = LoggerFactory.getLogger(S3.class);

    public void createBucket(DeployerConfig.S3Config s3Config) {
        try {
            String bucketName = s3Config.getBucketName();
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
            if (s3.doesBucketExistV2(bucketName)) {
                LOG.info("Bucket {} already exists", s3Config.getBucketName());
                return;
            } else {
                s3.createBucket(bucketName);
            }
            LOG.info("Bucket created {}", bucketName);
        } catch (Exception e) {
            LOG.error("Bucket create failed", e);
            throw new DeployerException("bucket_create_failed", e);
        }
    }

    public void uploadArtifact(DeployerConfig.S3Config s3Config) {
        try {
            LOG.info("Start uploading artifact");
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(new File(s3Config.getArtifactPath()).length());

            InputStream is = new FileInputStream(s3Config.getArtifactPath());
            PutObjectRequest request = new PutObjectRequest(s3Config.getBucketName(), s3Config.getObjectKey(), is, metadata);

            s3Client.putObject(request);
            LOG.info("Successfully uploaded artifact");
        } catch (Exception e) {
            LOG.error("Artifact upload failed", e);
            throw new DeployerException("upload_artifact_failed", e);
        }

    }
}
