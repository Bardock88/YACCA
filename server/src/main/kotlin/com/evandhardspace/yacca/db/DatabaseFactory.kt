package com.evandhardspace.yacca.db

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.net.URI

fun initDatabase() {
    val dbConfig = HoconApplicationConfig(ConfigFactory.load()).config("ktor.database")
    val uri = URI(dbConfig.property("uri").getString())
    val userInfo = uri.userInfo.split(":")
    val user = userInfo[0]
    val password = userInfo[1]
    val host = uri.host
    val port = uri.port
    val database = uri.path.split("/")[1]
    val jdbcUrl = "jdbc:postgresql://$host:$port/$database"

    Flyway.configure()
        .dataSource(jdbcUrl, user, password)
        .locations("classpath:db/migration")
        .load()
        .migrate()

    Database.connect(
        url = jdbcUrl,
        driver = dbConfig.property("driver").getString(),
        user = user,
        password = password
    )
}