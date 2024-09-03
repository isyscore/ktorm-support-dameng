import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val nexusUsername: String by project
val nexusPassword: String by project

plugins {
    java
    kotlin("jvm") version "1.9.24"
    // id("ktorm.module")
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ktorm:ktorm-core:3.6.0")

    testImplementation(kotlin("test"))
    testImplementation("com.dameng:DmJdbcDriver8:8.1.3.162")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks {
    val sourcesJar by creating(Jar::class) {
        dependsOn(classes)
        archiveClassifier = "sources"
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(javadoc)
        archiveClassifier = "javadoc"
        from(javadoc.get().destinationDir)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    publishing {

        publications {
            create<MavenPublication>("mavenKotlin") {
                groupId = "com.github.isyscore"
                artifactId = "ktorm-support-dameng"
                version = "1.0.0"
                from(getComponents()["java"])
                artifact(sourcesJar)
                artifact(javadocJar)
                pom {
                    name = "ktorm-support-dameng"
                    description = "ktorm-support-dameng"
                    url = "https://github.com/isyscore/ktorm-support-dameng"
                    packaging = "jar"

                    licenses {
                        license {
                            name = "ISYSCORE-LICENSE"
                            url = "https://github.com/isyscore/ktorm-support-dameng/LICENSE"
                        }
                    }

                    developers {
                        developer {
                            id = "isyscore"
                            name = "isyscore"
                            email = "os@isyscore.com"
                        }
                    }
                    scm {
                        connection = "https://github.com/isyscore/ktorm-support-dameng"
                        developerConnection = "https://github.com/isyscore/ktorm-support-dameng"
                        url = "https://github.com/isyscore/ktorm-support-dameng"
                    }
                }
            }
        }

        repositories {
            maven {
                name = "Release"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = "$nexusUsername"
                    password = "$nexusPassword"
                }
            }
        }
    }

    signing {
        sign(publishing.publications["mavenKotlin"])
    }
}
