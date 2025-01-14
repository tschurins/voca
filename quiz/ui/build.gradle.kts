plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":vocaquiz"))

    implementation("org.openjfx:javafx-base:23.0.1:linux")
    implementation("org.openjfx:javafx-controls:23.0.1:linux")
    implementation("org.openjfx:javafx-graphics:23.0.1:linux")
}

application {
    // Define the main class for the application.
    mainClass = "jal.voca.ui.QuizAppKt"
}
