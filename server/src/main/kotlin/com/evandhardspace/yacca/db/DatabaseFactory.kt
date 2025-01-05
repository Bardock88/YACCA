package com.evandhardspace.yacca.db

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDatabase() {
    val dbConfig = HoconApplicationConfig(ConfigFactory.load()).config("ktor.database")
    Database.connect(
        url = dbConfig.property("url").getString(),
        driver = dbConfig.property("driver").getString(),
        user = dbConfig.property("user").getString(),
        password = dbConfig.property("password").getString()
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, FavoriteCurrencies)
    }
}