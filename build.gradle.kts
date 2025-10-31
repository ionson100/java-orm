plugins {
    id("java")

}

group = "org.orm"
version = "1.0.3"

repositories {
    mavenCentral()
}

tasks.register<Jar>("fatJarWithSources") {
    group = "build"
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Классы
    from(sourceSets.main.get().output)

    // Исходники
    from(sourceSets.main.get().allSource) {
        into("sources")
    }

    // Добавляем зависимости
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })

    manifest {
        attributes["Main-Class"] = "com.orm"
    }
}


//tasks.register<Jar>("fatJar") {
//    group = "build"
//    archiveClassifier.set("all")
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//
//    from(sourceSets.main.get().output)
//
//    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get()
//            .filter { it.name.endsWith(".jar") }
//            .map { zipTree(it) }
//    })
//
//    manifest {
//        attributes["Main-Class"] = "com.orm" // укажи свою Main
//    }
//}
tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")

}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.13.2")
    // https://mvnrepository.com/artifact/commons-dbcp/commons-dbcp
    //implementation("commons-dbcp:commons-dbcp:1.4")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-pool2
    //implementation("org.apache.commons:commons-pool2:2.12.1")
    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:26.0.2-1")
    // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")


    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.8")

    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    //implementation("com.zaxxer:HikariCP:5.0.0")
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    implementation("com.zaxxer:HikariCP:5.1.0")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.17")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j:slf4j-simple:2.0.17")


    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
    implementation(group = "com.mysql", name = "mysql-connector-j", version = "9.5.0")
    //implementation("mysql:mysql-connector-java:9.5.0")

//    // https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc
//    implementation("com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11")
//
//    // https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc_auth
//    implementation("com.microsoft.sqlserver:mssql-jdbc_auth:13.2.1.x86")

}

tasks.test {
    useJUnitPlatform()
}