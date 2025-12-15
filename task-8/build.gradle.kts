plugins {
    id("spring-boot-conventions")
    application
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-session-jdbc")
}

application {
    mainClass.set("iits.workshop.htmx.Application")
}