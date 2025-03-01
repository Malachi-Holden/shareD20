package com.holden.dm

import com.holden.game.GameEntity
import com.holden.game.GamesTable
import com.holden.player.PlayerEntity
import com.holden.player.PlayersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object DMsTable : IntIdTable() {
    val name = varchar("name", 50)
    val gameCode = reference(
        "game_code",
        GamesTable,
        onDelete = ReferenceOption.CASCADE
    ).uniqueIndex()
    val playerId = reference(
        "player_id",
        PlayersTable,
        onDelete = ReferenceOption.CASCADE
    ).uniqueIndex()
}

class DMEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<DMEntity>(DMsTable)
    var name by DMsTable.name
    var game by GameEntity referencedOn DMsTable.gameCode
    var player by PlayerEntity referencedOn DMsTable.playerId
}

fun DMEntity.toModel() = DM(id.value, player.id.value, name, game.code.value)