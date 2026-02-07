plugins {
    id("java")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(fileTree("libs"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("XpBar")
    archiveVersion.set("1.0.2")
    from("src/main/resources")
}
