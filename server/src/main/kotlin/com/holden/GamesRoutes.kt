package com.holden

import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.gamesRoutes(repository: D20Repository) = route("/games") {
    post {
        try {
            val game = call.receive<Game>()
            val newGame = repository.addGame(game)
            call.respond(newGame)
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/{id}") {
        val id = call.pathParameters["id"]
        repository.getGame(id)
            ?.let {
                call.respond(it)
            }
            ?: call.respond(HttpStatusCode.NotFound)
    }

    delete("/{id}") {
        val id = call.pathParameters["id"]
        if (repository.deleteGame(id)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}