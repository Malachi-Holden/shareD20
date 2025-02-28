package com.holden.dieRolls

import com.holden.DieRollsRepository
import com.holden.InvalidDieRollId
import com.holden.InvalidGameCode
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.game.GameEntity
import org.jetbrains.exposed.sql.transactions.transaction

class DieRollsPostgresRepository: DieRollsRepository {
    override suspend fun create(form: DieRollForm): DieRoll = transaction {
        DieRollEntity.new {
            value = form.value
            game = GameEntity.findById(form.gameCode) ?: throw InvalidGameCode(form.gameCode)
            visibility = form.visibility.ordinal
        }.toModel()
    }

    override suspend fun read(id: Int): DieRoll = transaction {
        DieRollEntity.findById(id)?.toModel() ?: throw InvalidDieRollId(id)
    }

    override suspend fun delete(id: Int) = transaction {
        DieRollEntity.findById(id)?.delete() ?: throw InvalidDieRollId(id)
    }
}