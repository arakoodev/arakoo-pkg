package com.awslambdaconvert.plugin;

import com.awslambdaconvert.aws.deployment.Deployer;
import com.awslambdaconvert.aws.deployment.config.DeployerConfig;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginHandler implements Plugin<Project> {

    private final Deployer deployer;

    public PluginHandler() {
        this.deployer = new Deployer();
    }

    @Override
    public void apply(Project project) {
        PluginExtension extension =
                project.getExtensions().create("deployConfigs", PluginExtension.class);

        // TODO need to fx this is only build plugin project
        project.getTasks().register("buildZipTask", Zip.class, task -> {
            task.dependsOn("compileJava", "processResources");
            task.from("compileJava");
            task.from("processResources");
            task.into("lib")
                    .from(project.getConfigurations().getByName("runtimeClasspath"));
        });

        project.task("deploy")
                .dependsOn("buildZip")
                .doLast(task->{
            String buildPath = project.getBuildDir().getAbsolutePath();
            System.out.println("DEPLOYMENT START");

            String bucketName = project.getName().toLowerCase();
            String objectKey = String.format("%s-%s.zip", project.getName(), project.getVersion());
            String artifactPath = String.format("%s%s%s%s%s",
                    buildPath, File.separator, "distributions", File.separator, objectKey);
            Map<String, String> environmentVariables = prepareEnvironmentVariables(extension);
            String functionName = String.format("%s", project.getName()).toLowerCase();

            deployer.deploy(
                    new DeployerConfig(
                            new DeployerConfig.S3Config(
                                    bucketName, objectKey, artifactPath
                            ), new DeployerConfig.LambdaConfig(functionName, environmentVariables)));

            System.out.println("DEPLOYMENT COMPLETED");
        });
    }

    private Map<String, String> prepareEnvironmentVariables(PluginExtension extension) {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("APPLICATION_START_CLASS", extension.getSpringAppClass().get());
        // TODO set user defined EnvironmentVariables
        return environmentVariables;
    }
}
