## Prerequisites
#### AWS Account
#### [Set up AWS Credentials and Region on your local environment](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html)


## Spring-to-AWS-Lambda Plugin project
#### jump into spring-to-aws-lambda-plugin
#### run below commond to publish plugin into Maven Local
```
gradle clean publishToMavenLocal
```

## Sample Spring web project
#### jump into spring-demo-api
#### Add these plugin configurations into build.gradle
```json
deployConfigs {
	springAppClass = 'com.springdemo.Application' // Spring Starter Class
}
```

#### Add this task into build.gradle. !!! Note - This will move to plugin. then End-User don't need to add this !!!
```json
task buildZip(type: Zip) {
	from compileJava
	from processResources
	into('lib') {
		from configurations.runtimeClasspath
	}
}
```

#### run below commond to publish plugin into Maven Local
```
gradle -i  clean deploy
```
#### Endpoint URL will be return once after completed the below commond
#### Lambda function name will be the same as your Gradle project name