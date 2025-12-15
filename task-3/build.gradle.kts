plugins {
    id("spring-boot-conventions")
    application
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

application {
    mainClass.set("iits.workshop.htmx.Application")
}