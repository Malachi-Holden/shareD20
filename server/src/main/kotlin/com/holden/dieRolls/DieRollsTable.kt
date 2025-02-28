package com.holden.dieRolls

import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import com.holden.player.PlayerEntity
import com.holden.player.PlayersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object DieRollsTable: IntIdTable() {
    val gameCode = reference(
        "game_code",
        GamesTable,
        onDelete = ReferenceOption.CASCADE
    )
    val rolledBy = reference(
        "rolled_by",
        PlayersTable,
        onDelete = ReferenceOption.CASCADE
    )
    val value = integer("value")
    val visibility = integer("visibility")
    val fromDM = bool("from_dm")
}

class DieRollEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<DieRollEntity>(DieRollsTable)
    var value by DieRollsTable.value
    var game by GameEntity referencedOn DieRollsTable.gameCode
    var rolledBy by PlayerEntity referencedOn DieRollsTable.rolledBy
    var visibility by DieRollsTable.visibility
    var fromDM by DieRollsTable.fromDM
}

fun DieRollEntity.toModel() = DieRoll(
    id.value,
    game.code.value,
    rolledBy.id.value,
    value,
    DieRollVisibility.entries[visibility],
    fromDM
)