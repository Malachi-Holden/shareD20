package com.holden

import com.holden.di.ConnectionType
import com.holden.di.initKoin
import com.holden.game.GamesTable
import com.holden.game.gamesRoutes
import com.holden.dm.DMsTable
import com.holden.player.PlayersTable
import com.holden.dm.dmsRoutes
import com.holden.player.playersRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named

fun main(args: Array<String>) {
    val koin = initKoin()
    koin.get<DatabaseFactory>(named(ConnectionType.getFromArgs(args))).connect()
    transaction {
        SchemaUtils.create(PlayersTable)
        SchemaUtils.create(DMsTable)
        SchemaUtils.create(GamesTable)
    }
    val module = repositoryModule(koin.get(), Application::module)
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = module)
        .start(wait = true)
}

fun repositoryModule(
    repository: D20Repository,
    module: Application.(D20Repository) -> Unit
): Application.() -> Unit = { module(repository) }

fun Application.module(
    repository: D20Repository
) {
    install(CORS){ // to allow testing on localhost
        allowHost("localhost:8081", schemes = listOf("http", "https"))
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
    }
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Welcome to shareD20")
        }
        gamesRoutes(repository)
        playersRoutes(repository)
        dmsRoutes(repository)
    }
}