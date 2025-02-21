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
        TODO("Not yet implemented")
    }

    override suspend fun getGameByCode(code: String?): Game {
        val response = client.get("/games/$code")
        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body()
            }
            HttpStatusCode.NotFound -> {
                throw InvalidGameCode(code)
            }
            else -> {
                throw getHttpError(response)
            }
        }
    }

    override suspend fun hasGameWithCode(code: String?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createPlayer(form: PlayerForm): Player {
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(form)
        }
        when (response.status) {
            HttpStatusCode.OK -> return response.body()
            HttpStatusCode.NotFound -> throw InvalidGameCode(form.gameCode)
            else -> {
                throw getHttpError(response)
            }
        }
    }

    override suspend fun deletePlayer(id: Int?) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayer(id: Int?): Player {
        TODO("Not yet implemented")
    }

    override suspend fun hasPlayer(id: Int?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getDM(id: Int?): DM {
        TODO("Not yet implemented")
    }

    override suspend fun hasDM(id: Int?): Boolean {
        TODO("Not yet implemented")
    }

    suspend fun getHttpError(response: HttpResponse): Exception {
        return if (response.status == HttpStatusCode.InternalServerError) {
            InternalServerError(response.bodyAsText())
        } else {
            GenericHttpError(response.bodyAsText())
        }
    }
}