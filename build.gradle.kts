plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    detekt {
        buildUponDefaultConfig = true
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
