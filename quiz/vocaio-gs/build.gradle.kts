plugins {
    `java-library`
    `maven-publish`

    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":vocaquiz"))

    implementation("com.google.apis:google-api-services-sheets:v4-rev20241203-2.0.0")

    // tests
    testImplementation(kotlin("test"))
}
tasks.build {
    dependsOn("checkApikeyFile")
}
tasks.register("checkApikeyFile") {
  val projectDir = project.projectDir
  doFirst {
    val file = File(projectDir, "src/main/resources/jal/voca/lang/io/apikey")
    if (!file.exists()) throw RuntimeException("apikey file does not exist at " + file.absolutePath)
  }
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
            artifactId = "vocaio-gs"
            version = "0.1-SNAPSHOT"
        }
    }
    repositories {
        mavenLocal()
    }
}
