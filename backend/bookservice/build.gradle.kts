import com.google.protobuf.gradle.*

plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.8.19"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.google.protobuf:protobuf-java:3.25.3")
    implementation("io.grpc:grpc-netty-shaded:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:7.0.5.Final")
    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.51.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                java { }
            }
            task.plugins {
                id("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/java")
            srcDir("build/generated/source/proto/main/grpc")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
