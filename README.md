# Server Maven Plugin

[![Build Status](https://travis-ci.org/CJSCommonPlatform/server-maven-plugin.svg?branch=master)](https://travis-ci.org/CJSCommonPlatform/server-maven-plugin) [![Coverage Status](https://coveralls.io/repos/github/CJSCommonPlatform/server-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/CJSCommonPlatform/server-maven-plugin?branch=master)

A maven plugin that can be used to start and stop a server (like embedded Artemis) within a maven test cycle.

##Example usage in pom.xml

```xml
    <plugin>
	<groupId>uk.gov.justice.plugin</groupId>
	<artifactId>server-maven-plugin</artifactId>
	<configuration>
		<!-- The port used to stop the spawned process -->
		<port>9092</port>
		<!-- The name of the class used to start the embedded server -->
        <serverClass>uk.gov.justice.artemis.EmbeddedArtemisServer</serverClass>
	</configuration>
	<executions>
		<execution>
			<id>Spawn a new Artemis server</id>
			<phase>process-test-classes</phase>
			<goals>
				<goal>start</goal>
			</goals>
		</execution>
		<execution>
			<id>Stop a spawned Artemis server</id>
			<phase>verify</phase>
			<goals>
				<goal>stop</goal>
			 </goals>
		</execution>
	</executions>
	<dependencies>
		<dependency>
			<!-- Add all the dependencies required for the spawned Server to run -->			
		</dependency>
	</dependencies>
    </plugin>
```

The plugin starts a server using the supplied serverClass during the process-test-classes phase and shuts it down during the verify phase.

##Running the plugin

<code>
mvn verify
</code>

##Tip

Checkout the example-it project in the microservice_framework for a concrete example
