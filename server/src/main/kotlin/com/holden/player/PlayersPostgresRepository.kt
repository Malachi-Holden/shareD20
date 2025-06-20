package com.holden.player

import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollVisibility
import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.DieRollsTable
import com.holden.dieRolls.toModel
import com.holden.dm.DMEntity
import com.holden.dm.DMsTable
import com.holden.game.Game
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
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

    override suspend fun retrieveDieRolls(playerId: Int): List<DieRoll> = transaction {
        val player = PlayerEntity.findById(playerId) ?: throw InvalidPlayerId(playerId)
        player.dieRolls.map { it.toModel() }
    }

    override suspend fun retrieveVisibleDieRolls(playerId: Int): List<DieRoll> = transaction {
        val player = PlayerEntity.findById(playerId) ?: throw InvalidPlayerId(playerId)
        val isDM = DMEntity.findById(playerId) != null
        val gameCode = player.game.code
        var predicate = DieRollsTable.gameCode eq gameCode
        if (!isDM) {
            predicate = predicate and (DieRollsTable.visibility eq DieRollVisibility.All.ordinal or
                    ((DieRollsTable.visibility eq DieRollVisibility.PrivateDM.ordinal) and (DieRollsTable.rolledBy eq playerId)))
        }
        val query = DieRollsTable.innerJoin(GamesTable)
            .select(DieRollsTable.columns)
            .where(predicate)

        DieRollEntity.wrapRows(query).map { it.toModel() }
    }
}