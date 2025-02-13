package com.holden

import com.holden.games.gamesRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(
    repository: D20Repository = InMemoryD20Repository()
) {
    databaseFactory()
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
    }
}