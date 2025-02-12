dependencies {
    implementation(project(":core"))

    // TODO: Download these on runtime.
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")

    compileOnly("com.velocitypowered:velocity-api:3.1.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.0")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
