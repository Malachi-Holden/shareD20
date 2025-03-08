package com.holden.player

import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.DieRollsTable
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PlayersTable : IntIdTable() {
    val name = varchar("name", 50)
    val gameCode = reference(
        "game_code",
        GamesTable,
        onDelete = ReferenceOption.CASCADE
    )
}

class PlayerEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PlayerEntity>(PlayersTable)
    var name by PlayersTable.name
    var game by GameEntity referencedOn PlayersTable.gameCode
    val dieRolls by DieRollEntity referrersOn DieRollsTable.rolledBy
}

fun PlayerEntity.toModel() = Player(id.value, name, game.code.value)