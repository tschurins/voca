plugins {
    `java-library`
    `maven-publish`

    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    // tests
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        // Make sure output from standard out or error is shown in Gradle output.
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "jal.voca"
            artifactId = "vocaquiz"
            version = "0.1-SNAPSHOT"
        }
    }
    repositories {
        mavenLocal()
    }
}
