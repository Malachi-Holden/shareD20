package com.holden.players

import com.holden.Player
import com.holden.games.GameEntity
import com.holden.games.GamesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PlayersTable : IntIdTable() {
    val name = varchar("name", 50)
    val isDM = bool("is_dm")
    val gameCode = reference(
        "game_code",
        GamesTable,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()
}

class PlayerEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PlayerEntity>(PlayersTable)
    var name by PlayersTable.name
    var isDM by PlayersTable.isDM
    var game by GameEntity optionalReferencedOn PlayersTable.gameCode
}

fun PlayerEntity.toModel() = Player(id.value, name, isDM, game?.code?.value)