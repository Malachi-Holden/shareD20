package com.holden.dm

import com.holden.DMsRepository
import com.holden.getHttpError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DMsClientRepository: DMsRepository, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: Pair<DMForm, String>): DM {
        error("Cannot directly create DM client-side. Try creating a game instead")
    }

    override suspend fun read(id: Int): DM {
        val response = client.get("/dms/$id")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, id)
        }
        return response.body()
    }

    override suspend fun delete(id: Int) {
        // no op
        // no endpoint to delete dm
        // happens when game is deleted
    }
}