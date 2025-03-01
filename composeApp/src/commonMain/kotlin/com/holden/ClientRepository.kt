package com.holden

import com.holden.dieRoll.DieRollsClientRepository
import com.holden.dm.DMsClientRepository
import com.holden.game.GamesClientRepository
import com.holden.player.PlayersClientRepository
import io.ktor.client.statement.*
import io.ktor.http.*

class ClientRepository: D20Repository {
    override val gamesRepository = GamesClientRepository()
    override val playersRepository = PlayersClientRepository()
    override val dmsRepository = DMsClientRepository()
    override val dieRollsRepository = DieRollsClientRepository()
}

suspend fun getHttpError(
    response: HttpResponse,
    code: String?,
    id: Int?
): Exception {
    val body = response.bodyAsText()
    return when (response.status) {
        HttpStatusCode.NotFound -> {
            when (body) {
                "InvalidGameCode" -> InvalidGameCode(code)
                "InvalidPlayerId" -> InvalidPlayerId(id)
                "InvalidDMId" -> InvalidDMId(id)
                "InvalidDieRollId" -> InvalidDieRollId(id)
                "NoDMFoundWithGameCode" -> NoDMFoundWithGameCode(code)
                else -> GenericHttpError("")
            }
        }
        HttpStatusCode.InternalServerError -> InternalServerError(response.bodyAsText())
        else -> GenericHttpError(response.bodyAsText())
    }
}