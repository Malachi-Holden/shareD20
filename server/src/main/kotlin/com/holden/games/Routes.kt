package com.holden.games

import com.holden.D20RepositoryOld
import com.holden.InvalidGameCode
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.gamesRoutes(repository: D20RepositoryOld) = route("/games") {
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
        try {
            call.respond(repository.getGameByCode(code))
        } catch (e: InvalidGameCode) {
            call.respond(HttpStatusCode.NotFound, "InvalidGameCode")
        }
    }

    delete("/{code}") {
        val code = call.pathParameters["code"]
        try {
            repository.deleteGame(code)
            call.respond(HttpStatusCode.NoContent)
        } catch (e: InvalidGameCode) {
            call.respond(HttpStatusCode.NotFound, "InvalidGameCode")
        }
    }
}