package com.holden

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


class ClientRepository(val client: HttpClient): D20Repository {
    override suspend fun addGame(form: GameForm): Game {
        return client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(form)
        }.body()
    }

    override suspend fun deleteGame(code: String?) {
        if (code == null) {
            throw InvalidGameCode(null)
        }
        val response = client.delete("/games/$code")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, code, null)
        }
    }

    override suspend fun getGameByCode(code: String?): Game {
        val response = client.get("/games/$code")
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw getHttpError(response, code, null)
    }

    override suspend fun hasGameWithCode(code: String?): Boolean {
        return try {
            getGameByCode(code)
            true
        } catch (e: InvalidGameCode) {
            false
        }
    }

    override suspend fun createPlayer(form: PlayerForm): Player {
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(form)
        }
        if (!response.status.isSuccess()) {
            throw getHttpError(response, form.gameCode, null)
        }
        return response.body()
    }

    override suspend fun deletePlayer(id: Int?) {
        if (id == null) {
            throw InvalidPlayerId(null)
        }
        val response = client.delete("/players/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
    }

    override suspend fun getPlayer(id: Int?): Player {
        if (id == null) {
            throw InvalidPlayerId(null)
        }
        val response = client.get("/players/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
        return response.body()
    }

    override suspend fun hasPlayer(id: Int?): Boolean {
        return try {
            getPlayer(id)
            true
        } catch (e: InvalidPlayerId) {
            false
        }
    }

    override suspend fun getDM(id: Int?): DM {
        if (id == null) {
            throw InvalidDMId(null)
        }
        val response = client.get("/dms/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
        return response.body()
    }

    override suspend fun hasDM(id: Int?): Boolean {
        return try {
            getDM(id)
            true
        } catch (e: InvalidDMId) {
            false
        }
    }

    private suspend fun getHttpError(
        response: HttpResponse,
        code: String?,
        id: Int?
    ): Exception {
        return when (response.status) {
            HttpStatusCode.NotFound -> {
                when (response.bodyAsText()) {
                    "InvalidGameCode" -> InvalidGameCode(code)
                    "InvalidPlayerId" -> InvalidPlayerId(id)
                    "InvalidDMId" -> InvalidDMId(id)
                    "NoDMFoundWithGameCode" -> InvalidGameCode(code)
                    else -> GenericHttpError("")
                }
            }
            HttpStatusCode.InternalServerError -> InternalServerError(response.bodyAsText())
            else -> GenericHttpError(response.bodyAsText())
        }
    }
}