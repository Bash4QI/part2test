import com.android.build.gradle.BaseExtension
import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.1")
        // النسخة المستقرة الرسمية لبلجن البناء لتفادي مشاكل الـ SNAPSHOT
        classpath("com.github.recloudstream.gradle:gradle:81b1d424d")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.cloudstream(configuration: CloudstreamExtension.() -> Unit) = extensions.getByName<CloudstreamExtension>("cloudstream").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "https://github.com/user/repo")
    }
 
    android {
        namespace = "com.luna712"

        defaultConfig {
            minSdk = 21
            compileSdkVersion(36)
            targetSdk = 36
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_1_8) // Required
                freeCompilerArgs.addAll(
                    "-Xno-call-assertions",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions"
                )
            }
        }
    }

    dependencies {
        val implementation by configurations

        // استبدال الـ SNAPSHOT بنسخة ثابتة ومستقرة لحل مشكلة الـ Read timed out نهائياً
        implementation("com.github.recloudstream.cloudstream:library:81b1d424d")

        implementation(kotlin("stdlib")) 
        implementation("com.github.Blatzar:NiceHttp:0.4.13") 
        implementation("org.jsoup:jsoup:1.21.2") 
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.5") 
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
