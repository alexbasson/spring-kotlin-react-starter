defaultTasks("build")

tasks.wrapper {
    description = "Regenerates the Gradle Wrapper files"
    gradleVersion = "6.2.2"
    distributionUrl = "https://services.gradle.org/distributions/gradle-${gradleVersion}-all.zip"
}

