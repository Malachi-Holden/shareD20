package com.holden.dieRolls

import com.holden.InvalidDieRollId
import com.holden.InvalidGameCode
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollsRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.dieRollsRoutes(repository: DieRollsRepository) = route("/dieRolls") {
    post {
        try {
            val form = call.receive<DieRollForm>()
            val newDieRoll = repository.create(form)
            call.respond(newDieRoll)
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
            call.respond(repository.retrieve(id ?: throw InvalidDieRollId(null)))
        } catch (e: InvalidDieRollId) {
            call.respond(HttpStatusCode.NotFound, "InvalidDieRollId")
        }
    }

    delete("/{id}") {
        val id = call.pathParameters["id"]?.toInt()
        try {
            repository.delete(id  ?: throw InvalidDieRollId(null))
            call.respond(HttpStatusCode.NoContent)
        } catch (e: InvalidDieRollId) {
            call.respond(HttpStatusCode.NotFound, "InvalidDieRollId")
        }
    }
}