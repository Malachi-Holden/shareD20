package com.holden
import io.github.cdimascio.dotenv.Dotenv
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

fun databaseFactory() {
    val dotenv = Dotenv.load()

    val config = HikariConfig().apply {
        jdbcUrl = dotenv["POSTGRES_URL"]
        driverClassName = "org.postgresql.Driver"
        username = dotenv["POSTGRES_USER"]
        password = dotenv["POSTGRES_PASSWORD"]
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
}