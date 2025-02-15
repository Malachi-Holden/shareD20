package com.holden.players

import com.holden.D20Repository
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
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        repository.getPlayer(id)?.let {
            call.respond(it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }

    delete("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        if (repository.deletePlayer(id)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}