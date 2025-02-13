package com.holden.games

import org.jetbrains.exposed.sql.Table

val GAME_CODE_LENGTH = 8
object GamesTable: Table("games") {
    val code = varchar("code", GAME_CODE_LENGTH)
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(code)
}