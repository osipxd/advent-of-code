plugins {
    kotlin("jvm") version "1.6.0" apply false
}

subprojects {
    apply(plugin = "kotlin")

    dependencies {
        "testImplementation"(project(":utils"))
        "testImplementation"(kotlin("test-junit5"))
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.8.2")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:5.8.2")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine")
    }

    repositories {
        mavenCentral()
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}
