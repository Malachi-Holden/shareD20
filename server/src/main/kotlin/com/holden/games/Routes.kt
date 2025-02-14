package com.holden.games

import com.holden.D20Repository
import com.holden.GameForm
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.gamesRoutes(repository: D20Repository) = route("/games") {
    post {
        try {
            val form = call.receive<GameForm>()
            val newGame = repository.addGame(form)
            call.respond(newGame)
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/{code}") {
        val code = call.pathParameters["code"]
        repository.getGameByCode(code)
            ?.let {
                call.respond(it)
            }
            ?: call.respond(HttpStatusCode.NotFound)
    }

    delete("/{code}") {
        val code = call.pathParameters["code"]
        if (repository.deleteGame(code)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}