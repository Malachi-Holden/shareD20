package com.holden.players

import com.holden.CrdRepository
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.games.GameEntity
import org.jetbrains.exposed.sql.transactions.transaction

class PlayersPostgresRepository: CrdRepository<Int, PlayerForm, Player> {
    override suspend fun create(form: PlayerForm): Player = transaction {
        PlayerEntity.new {
            name = form.name
            game = GameEntity
                .findById(form.gameCode.uppercase())
                ?: throw InvalidGameCode(form.gameCode)
        }.toModel()
    }

    override suspend fun read(id: Int): Player = transaction {
        PlayerEntity
            .findById(id)
            ?.toModel()
            ?: throw InvalidPlayerId(id)
    }

    override suspend fun delete(id: Int) = transaction {
        val player = PlayerEntity.findById(id)
        player?.delete() ?: throw InvalidPlayerId(id)
    }
}