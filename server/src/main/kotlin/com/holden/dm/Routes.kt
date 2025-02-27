package com.holden.dm

import com.holden.D20Repository
import com.holden.InvalidDMId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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