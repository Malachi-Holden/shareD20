package com.holden.dieRolls

import com.holden.InvalidDieRollId
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollsRepository
import com.holden.game.GameEntity
import com.holden.player.PlayerEntity
import org.jetbrains.exposed.sql.transactions.transaction

class DieRollsPostgresRepository: DieRollsRepository {
    override suspend fun create(form: DieRollForm): DieRoll = transaction {
        val rolledByPlayer = PlayerEntity.findById(form.rolledBy) ?: throw InvalidPlayerId(form.rolledBy)
        DieRollEntity.new {
            value = form.value
            game = GameEntity.findById(form.gameCode) ?: throw InvalidGameCode(form.gameCode)
            rolledBy = rolledByPlayer
            visibility = form.visibility.ordinal
            fromDM = form.fromDM
        }.toModel()
    }

    override suspend fun retrieve(id: Int): DieRoll = transaction {
        DieRollEntity.findById(id)?.toModel() ?: throw InvalidDieRollId(id)
    }

    override suspend fun delete(id: Int) = transaction {
        DieRollEntity.findById(id)?.delete() ?: throw InvalidDieRollId(id)
    }
}