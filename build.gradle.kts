plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.programmers"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    testImplementation("org.springframework.security:spring-security-test")
    annotationProcessor ("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation ("org.slf4j:slf4j-api")
    implementation ("ch.qos.logback:logback-classic")

    implementation ("org.commonmark:commonmark:0.21.0")

    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // JPA 3.x 버전
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta") // QueryDSL JPA 의존성
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta") // Q클래스 생성용

    annotationProcessor ("jakarta.annotation:jakarta.annotation-api") // java.lang.NoClassDefFoundError (javax.annotation.Generated) 에러 대응 코드
    annotationProcessor ("jakarta.persistence:jakarta.persistence-api")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
