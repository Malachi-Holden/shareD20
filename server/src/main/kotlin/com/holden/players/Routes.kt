package com.holden.players

import com.holden.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.playersRoutes(repository: D20Repository) = route("players") {
    post {
        try {
            val form = call.receive<PlayerForm>()
            val newPlayer = repository.playersRepository.create(form)
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
        val id = call.pathParameters["id"]?.toInt()
        try {
            call.respond(repository.playersRepository.read(id ?: throw InvalidPlayerId(null)))
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, "InvalidPlayerId")
        }
    }

    delete("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        try {
            repository.playersRepository.delete(id  ?: throw InvalidPlayerId(null))
            call.respond(HttpStatusCode.NoContent)
        } catch (e: InvalidPlayerId) {
            call.respond(HttpStatusCode.NotFound, "InvalidPlayerId")
        }
    }
}

fun Routing.dmsRoutes(repository: D20Repository) = route("dms") {
    get("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        try {
            call.respond(repository.dmsRepository.read(id ?: throw InvalidDMId(null)))
        } catch (e: InvalidDMId) {
            call.respond(HttpStatusCode.NotFound, "InvalidDMId")
        }
    }
}