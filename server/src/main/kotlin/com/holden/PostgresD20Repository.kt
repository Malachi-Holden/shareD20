package com.holden

import com.holden.games.GameEntity
import com.holden.games.GamesTable
import com.holden.games.toModel
import com.holden.util.uniqueRandomStringIterator
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresD20Repository(
    private val generateCodes: Iterator<String> = uniqueRandomStringIterator(GAME_ID_LENGTH) { code ->
        GameEntity.findById(code) != null
    }
): D20Repository {
    init {
        transaction {
            SchemaUtils.create(GamesTable)
        }
    }
    override fun addGame(form: GameForm): Game = transaction {
        GameEntity.new(generateCodes.next()) {
            name = form.name
        }
    }.toModel()

    override fun deleteGame(code: String?): Boolean = transaction {
        val game = GameEntity.findById(code ?: return@transaction false)
        game?.delete()
        return@transaction game != null
    }

    override fun getGameByCode(code: String?): Game? = transaction {
        GameEntity.findById(code ?: return@transaction null)
    }?.toModel()

    override fun hasGameWithCode(code: String?): Boolean = getGameByCode(code) != null
}