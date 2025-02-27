package com.holden.game

import com.holden.CrdRepository
import com.holden.getHttpError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GamesClientRepository: CrdRepository<String, GameForm, Game>, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: GameForm): Game = client.post("/games") {
        contentType(ContentType.Application.Json)
        setBody(form)
    }.body()

    override suspend fun read(id: String): Game {
        val response = client.get("/games/$id")
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw getHttpError(response, id, null)
    }

    override suspend fun delete(id: String) {
        val response = client.delete("/games/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, id, null)
        }
    }
}