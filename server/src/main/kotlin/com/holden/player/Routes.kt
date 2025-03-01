package com.holden.player

import com.holden.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.playersRoutes(repository: PlayersRepository) = route("players") {
    post {
        try {
            val form = call.receive<PlayerForm>()
            val newPlayer = repository.create(form)
            call.respond(newPlayer)
        } catch (e: InvalidGameCode) {
            call.respond(HttpStatusCode.NotFound, "InvalidGameCode")
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/{id}") {
        val id = call.pathParameters["id"]?.toInt() ?: throw InvalidPlayerId(null)
        try {
            call.respond(repository.retrieve(id))
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, "InvalidPlayerId")
        }
    }

    get("/{id}/dieRolls") {
        val id = call.pathParameters["id"]?.toInt() ?: throw InvalidPlayerId(null)
        try {
            call.respond(repository.retreiveDieRolls(id))
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, "InvalidPlayerId")
        }
    }

    delete("/{id}") {
        val id = call.pathParameters["id"]?.toInt() ?: throw InvalidPlayerId(null)
        try {
            repository.delete(id)
            call.respond(HttpStatusCode.NoContent)
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, "InvalidPlayerId")
        }
    }
}

