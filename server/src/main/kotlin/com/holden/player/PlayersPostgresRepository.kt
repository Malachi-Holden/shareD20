package com.holden.player

import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dieRoll.DieRoll
import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.DieRollsTable
import com.holden.dieRolls.toModel
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PlayersPostgresRepository: PlayersRepository {
    override suspend fun create(form: PlayerForm): Player = transaction {
        PlayerEntity.new {
            name = form.name
            game = GameEntity
                .findById(form.gameCode.uppercase())
                ?: throw InvalidGameCode(form.gameCode)
        }.toModel()
    }

    override suspend fun retrieve(id: Int): Player = transaction {
        PlayerEntity
            .findById(id)
            ?.toModel()
            ?: throw InvalidPlayerId(id)
    }

    override suspend fun delete(id: Int) = transaction {
        val player = PlayerEntity.findById(id)
        player?.delete() ?: throw InvalidPlayerId(id)
    }

    override suspend fun retreiveDieRolls(playerId: Int): List<DieRoll> = transaction {
        if (PlayerEntity.findById(playerId) == null) throw InvalidPlayerId(playerId)
        DieRollEntity.find(DieRollsTable.rolledBy eq playerId).map { it.toModel() }
    }
}