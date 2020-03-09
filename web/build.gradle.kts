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

tasks.assemble {
    dependsOn("npm_run_build")
}
