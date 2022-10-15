package com.awslambdaconvert.aws.deployment.resources;

import com.awslambdaconvert.exception.DeployerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;

import java.util.List;

public class IAM extends AWSResource {
    private static final Logger LOG = LoggerFactory.getLogger(Lambda.class);

    private IamClient iam;

    public IAM() {
        iam = IamClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public String createIAMRole(String roleName, String assumePolicyJson ) {
        try {
            CreateRoleRequest request = CreateRoleRequest.builder()
                    .roleName(roleName)
                    .assumeRolePolicyDocument(assumePolicyJson)
                    .build();
            CreateRoleResponse response = iam.createRole(request);
            return response.role().arn();
        } catch (Exception e) {
            LOG.error("IAM role create failed", e);
            throw new DeployerException("iam_role_create_failed", e);
        }
    }

    public String createIAMPolicy(String policyName, String policyDocument) {

        try {
            // Create an IamWaiter object.
            IamWaiter iamWaiter = iam.waiter();
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(policyDocument).build();

            CreatePolicyResponse response = iam.createPolicy(request);

            // Wait until the policy is created.
            GetPolicyRequest polRequest = GetPolicyRequest.builder()
                    .policyArn(response.policy().arn())
                    .build();

            WaiterResponse<GetPolicyResponse> waitUntilPolicyExists = iamWaiter.waitUntilPolicyExists(polRequest);
            waitUntilPolicyExists.matched().response().ifPresent(System.out::println);
            return response.policy().arn();

        } catch (IamException e) {
            LOG.error("IAM policy create failed", e);
            throw new DeployerException("iam_policy_create_failed", e);
        }
    }

    public void attachIAMRolePolicy(String roleName, String policyArn) {

        try {
            ListAttachedRolePoliciesRequest request = ListAttachedRolePoliciesRequest.builder()
                    .roleName(roleName)
                    .build();

            ListAttachedRolePoliciesResponse response = iam.listAttachedRolePolicies(request);
            List<AttachedPolicy> attachedPolicies = response.attachedPolicies();

            String polArn;
            for (AttachedPolicy policy: attachedPolicies) {
                polArn = policy.policyArn();
                if (polArn.compareTo(policyArn)==0) {
                    LOG.info("{} policy is already attached to this role.", roleName);
                    return;
                }
            }

            AttachRolePolicyRequest attachRequest = AttachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(policyArn)
                    .build();

            iam.attachRolePolicy(attachRequest);
            LOG.info("{} Successfully attached role", roleName);
        } catch (IamException e) {
            LOG.error("IAM role policy attached failed", e);
            throw new DeployerException("iam_role_policy_attached_failed", e);
        }
    }

    public String getRoleInformation(String roleName) {

        try {
            GetRoleRequest roleRequest = GetRoleRequest.builder()
                    .roleName(roleName)
                    .build();

            GetRoleResponse response = iam.getRole(roleRequest) ;
            return response.role().arn();
        } catch (IamException e) {
            return null;
        }
    }
}
