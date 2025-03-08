package com.holden.game

import com.holden.getHttpError
import com.holden.player.Player
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GamesClientRepository: GamesRepository, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: GameForm): Game = client.post("/games") {
        contentType(ContentType.Application.Json)
        setBody(form)
    }.body()

    override suspend fun retrieve(id: String): Game {
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

    override suspend fun retreivePlayers(code: String): List<Player> {
        val response = client.get("/games/$code/players")
        if (!response.status.isSuccess()) {
            throw getHttpError(response, code, null)
        }
        return response.body()
    }
}

fun GamesRepository.allPlayersFlow(interval: Long = 30, game: Game) = flow {
    while (true) {
        val players = retreivePlayers(game.code)
        emit(players)
        delay(interval)
    }
}