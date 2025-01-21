plugins {
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
  doFirst {
    if (!File("./src/main/resources/jal/voca/lang/io/apikey").exists()) throw RuntimeException("apikey file does not exist")
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