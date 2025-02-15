package com.holden.games

import com.holden.Game
import com.holden.players.PlayerEntity
import com.holden.players.PlayersTable
import com.holden.players.toModel
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

val GAME_CODE_LENGTH = 8

object GamesTable: IdTable<String>("games") {
    override val id = varchar("code", GAME_CODE_LENGTH).entityId()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

class GameEntity(code: EntityID<String>): Entity<String>(code) {
    companion object : EntityClass<String, GameEntity>(GamesTable)
    var code by GamesTable.id
    var name by GamesTable.name
    val players by PlayerEntity optionalReferrersOn PlayersTable.gameCode
}

fun GameEntity.toModel() = Game(code = code.value, name = name, players = players.map { it.toModel() })