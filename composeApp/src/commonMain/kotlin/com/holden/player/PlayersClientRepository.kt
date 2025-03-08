package com.holden.player

import com.holden.dieRoll.DieRoll
import com.holden.game.Game
import com.holden.game.GamesRepository
import com.holden.getHttpError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayersClientRepository: PlayersRepository, KoinComponent {
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

    override suspend fun retrieve(id: Int): Player {
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

    override suspend fun retreiveDieRolls(playerId: Int): List<DieRoll> {
        val response = client.get("/players/$playerId/dieRolls")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, null, playerId)
        }
        return response.body()
    }
}
