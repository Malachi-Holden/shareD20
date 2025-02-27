package com.holden.dms

import com.holden.games.GameEntity
import com.holden.games.GamesTable
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
}

class DMEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<DMEntity>(DMsTable)
    var name by DMsTable.name
    var game by GameEntity referencedOn DMsTable.gameCode
}

fun DMEntity.toModel() = DM(id.value, name, game.code.value)