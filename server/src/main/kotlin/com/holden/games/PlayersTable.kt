package com.holden.games

import com.holden.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object PlayersTable : IntIdTable() {
    val name = varchar("name", 50)
    val isDM = bool("is_dm")
}

class PlayerEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PlayerEntity>(PlayersTable)
    var name by PlayersTable.name
    var isDM by PlayersTable.isDM
}

fun PlayerEntity.toModel() = Player(id.value, name, isDM)