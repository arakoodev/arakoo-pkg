package com.awslambdaconvert.plugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PluginHandlerTests {

	@Test
	void test() {
		Project project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("com.awslambdaconvert");

		System.out.println("getDisplayName " + project.getName());
		System.out.println("getDisplayName " + project.getVersion());

		assertNotNull(project.getTasks().getByName("deploy"));
	}

}
