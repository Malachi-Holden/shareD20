package com.holden.player

import com.holden.CrdRepository
import com.holden.getHttpError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayersClientRepository: CrdRepository<Int, PlayerForm, Player>, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: PlayerForm): Player {
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(form)
        }
        if (!response.status.isSuccess()) {
            throw getHttpError(response, form.gameCode, null)
        }
        return response.body()
    }

    override suspend fun read(id: Int): Player {
        val response = client.get("/players/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
        return response.body()
    }

    override suspend fun delete(id: Int) {
        val response = client.delete("/players/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
    }
}