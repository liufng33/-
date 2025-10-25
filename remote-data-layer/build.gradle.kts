plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

group = "com.remotedata"
version = "1.0.0"

dependencies {
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
    
    // Retrofit & OkHttp - using 'api' to expose to consuming modules
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.retrofit2:converter-simplexml:2.9.0")
    api("com.squareup.retrofit2:converter-scalars:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // JSON/XML Serialization
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.simpleframework:simple-xml:2.7.1")
    
    // Jsoup for HTML parsing
    implementation("org.jsoup:jsoup:1.17.1")
    
    // Dependency Injection - Koin
    implementation("io.insert-koin:koin-core:3.5.0")
    
    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.20")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
