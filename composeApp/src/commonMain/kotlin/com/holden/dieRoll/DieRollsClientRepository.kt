package com.holden.dieRoll

import com.holden.getHttpError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DieRollsClientRepository: DieRollsRepository, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: DieRollForm): DieRoll {
        val response = client.post("/dieRolls") {
            contentType(ContentType.Application.Json)
            setBody(form)
        }
        if (!response.status.isSuccess()) {
            throw getHttpError(response, form.gameCode, form.rolledBy)
        }
        return response.body()
    }

    override suspend fun retrieve(id: Int): DieRoll {
        val response = client.get("/dieRolls/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
        return response.body()
    }

    override suspend fun delete(id: Int) {
        val response = client.delete("/dieRolls/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
    }
}