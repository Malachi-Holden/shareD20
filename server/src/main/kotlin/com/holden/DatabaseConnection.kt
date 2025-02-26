package com.holden
import io.github.cdimascio.dotenv.Dotenv
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun interface DatabaseFactory {
    fun connect()
}

object PostGresDatabseFacotry: DatabaseFactory {
    override fun connect() {
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
}

object InMemoryDatabaseFactory: DatabaseFactory {
    override fun connect() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    }
}