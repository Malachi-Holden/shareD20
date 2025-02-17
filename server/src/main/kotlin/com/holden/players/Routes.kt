package com.holden.players

import com.holden.D20Repository
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.PlayerForm
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.playersRoutes(repository: D20Repository) = route("players") {
    post {
        try {
            val form = call.receive<PlayerForm>()
            val newPlayer = repository.createPlayer(form)
            call.respond(newPlayer)
        } catch (e: InvalidGameCode) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "")
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        try {
            call.respond(repository.getPlayer(id))
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "")
        }
    }

    delete("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        try {
            repository.deletePlayer(id)
            call.respond(HttpStatusCode.NoContent)
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "")
        }
    }
}