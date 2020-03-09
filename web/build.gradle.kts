import com.moowork.gradle.node.npm.NpmTask

buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.github.node-gradle:gradle-node-plugin:2.2.2")
    }
}

plugins {
    base
    id("com.github.node-gradle.node") version "2.2.2"
}

node {
    // version of node to use
    version = "13.8.0"

    // version of npm to use
    npmVersion = "6.13.7"

    // If true, it will download node using the above parameters
    // If false, it will try to use the globally installed node
    download = true
}

tasks.named<NpmTask>("npm_run_build") {
    inputs.files(fileTree("public"))
    inputs.files(fileTree("src"))

    inputs.file("package.json")
    inputs.file("package-lock.json")

    outputs.dir("build")
}

val packageNpmApp by tasks.registering(Jar::class) {
    dependsOn("npm_run_build")
    baseName = "web"
    extension = "jar"
    destinationDir = file("${projectDir}/build_packageNpmApp")
    from("build") {
        into("static")
    }
}

val npmResources by configurations.creating

configurations.named("default").get().extendsFrom(npmResources)

artifacts {
    add(npmResources.name, packageNpmApp.get().archivePath) {
        builtBy(packageNpmApp)
        type = "jar"
    }
}

tasks.assemble {
    dependsOn(packageNpmApp)
}

val testsExecutedMarkerName: String = "${projectDir}/.tests.executed"

val test by tasks.registering(NpmTask::class) {
    dependsOn("assemble")

    setEnvironment(mapOf("CI" to "true"))
    args = listOf("run", "test")

    inputs.files(fileTree("src"))
    inputs.file("package.json")
    inputs.file("package-lock.json")

    doLast {
        File(testsExecutedMarkerName).appendText("delete this file to force re-evaluation of the web tests")
    }

    outputs.file(testsExecutedMarkerName)
}

tasks.check {
    dependsOn(test)
}

tasks.clean {
    delete(packageNpmApp.get().archivePath)
    delete(testsExecutedMarkerName)
}
