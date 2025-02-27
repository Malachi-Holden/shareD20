package com.holden.dieRolls

import com.holden.dieRoll.DieRoll
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import com.holden.player.PlayersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object DieRollsTable: IntIdTable() {
    val value = integer("value")
    val gameCode = reference(
        "game_code",
        GamesTable,
        onDelete = ReferenceOption.CASCADE
    )
}

class DieRollEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<DieRollEntity>(DieRollsTable)
    var value by DieRollsTable.value
    var game by GameEntity referencedOn PlayersTable.gameCode
}

fun DieRollEntity.toModel() = DieRoll(id.value, value, game.code.value)