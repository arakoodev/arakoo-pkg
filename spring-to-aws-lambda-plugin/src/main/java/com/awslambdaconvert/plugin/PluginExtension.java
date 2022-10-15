package com.awslambdaconvert.plugin;

import org.gradle.api.provider.Property;

abstract public class PluginExtension {
    abstract public Property<String> getSpringAppClass();
}
