package com.evandhardspace.yacca.db

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    val dbConfig = HoconApplicationConfig(ConfigFactory.load()).config("ktor.database")
    Flyway.configure()
        .dataSource(
            dbConfig.property("url").getString(),
            dbConfig.property("user").getString(),
            dbConfig.property("password").getString(),
        )
        .locations("classpath:db/migration")
        .load()
        .migrate()

    Database.connect(
        url = dbConfig.property("url").getString(),
        driver = dbConfig.property("driver").getString(),
        user = dbConfig.property("user").getString(),
        password = dbConfig.property("password").getString()
    )
}