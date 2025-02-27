package com.holden.game

import com.holden.NoDMFoundWithGameCode
import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.DieRollsTable
import com.holden.dieRolls.toModel
import com.holden.dm.DMEntity
import com.holden.dm.DMsTable
import com.holden.dm.toModel
import com.holden.player.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
    val dm: DMEntity?
        get() = DMEntity.find(DMsTable.gameCode eq code).firstOrNull()
    val players by PlayerEntity referrersOn PlayersTable.gameCode
    val dieRolls by DieRollEntity referrersOn DieRollsTable.gameCode
}

fun GameEntity.toModel() = Game(
    code = code.value,
    name = name,
    dm = dm?.toModel() ?: throw NoDMFoundWithGameCode(code.value),
    players = players
        .map {
            it.toModel()
        },
    dieRolls = dieRolls
        .map {
            it.toModel()
        }
)