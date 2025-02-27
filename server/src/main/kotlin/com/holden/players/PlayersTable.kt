package com.holden.players

import com.holden.games.GameEntity
import com.holden.games.GamesTable
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
}

fun PlayerEntity.toModel() = Player(id.value, name, game.code.value)