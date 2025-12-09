import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqGenerate

val postgresVersion = "42.7.2"
val telegramBotVersion = "8.3.0"
val jooqVersion = "3.19.3"

plugins {
    id("nu.studer.jooq") version ("9.0")
    id("org.flywaydb.flyway") version ("9.22.3")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
}

group = "ru.telegram.bot.adapter"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17) // gradle 8.8
    }
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.clean {
    delete("src/main/java")
}

extra["springCloudVersion"] = "2023.0.3"

val flywayMigration = configurations.create("flywayMigration")

flyway {
    validateOnMigrate = false
    configurations = arrayOf("flywayMigration")
    url = (System.getenv("SPRING_DATASOURCE_URL") ?: "jdbc:postgresql://localhost:5432/bot")
    user = (System.getenv("SPRING_DATASOURCE_USERNAME") ?: "postgres")
    password = (System.getenv("SPRING_DATASOURCE_PASSWORD") ?: "postgres")
    cleanDisabled = false
}

dependencies {
    //data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")
    flywayMigration("org.postgresql:postgresql:$postgresVersion")
    jooqGenerator("org.postgresql:postgresql:$postgresVersion")
    // Kotlin JPA support
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Integration
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    // mechanism that helps to generate sql queries in a DSL-like language
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    // template engine for creating dynamic texts
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Telegram Api libs
    implementation("org.telegram:telegrambots-springboot-longpolling-starter:$telegramBotVersion")
    implementation("org.telegram:telegrambots-extensions:$telegramBotVersion")
    implementation("org.telegram:telegrambots-client:$telegramBotVersion")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
// ./gradlew generateJooq to generate POJO from db scheme
jooq {
    edition.set(JooqEdition.OSS)
    version.set(jooqVersion)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = flyway.url
                    user = flyway.user
                    password = flyway.password
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    generate.apply {
                        withPojos(true)
                        withDeprecated(false)
                        withRelations(true)
                        withRecords(true)
                        withPojosEqualsAndHashCode(true)
                        withFluentSetters(true)
                        withJavaTimeTypes(true)
                        isPojos = true
                        isPojosEqualsAndHashCode = true
                    }
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "flyway_schema_history|spatial_ref_sys|st_.*|_st.*"
                    }
                    target.apply {
                        packageName = "ru.telegram.bot.adapter.domain"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }

    tasks.named<JooqGenerate>("generateJooq").configure {
        inputs.files(fileTree("src/main/resources/db/migration"))
            .withPropertyName("migrations")
            .withPathSensitivity(PathSensitivity.RELATIVE)
        allInputsDeclared.set(true)
        outputs.upToDateWhen { false }
    }

    kotlin {
        jvmToolchain(17)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
