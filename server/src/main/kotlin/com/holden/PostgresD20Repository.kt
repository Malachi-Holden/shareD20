package com.holden

import com.holden.games.GameEntity
import com.holden.games.toModel
import com.holden.players.PlayerEntity
import com.holden.players.toModel
import com.holden.util.uniqueRandomStringIterator
import org.jetbrains.exposed.sql.transactions.transaction

val GAME_ID_LENGTH = 8 // in the future this could be set by the administrator

class PostgresD20Repository(
    private val generateCodes: Iterator<String> = uniqueRandomStringIterator(GAME_ID_LENGTH) { code ->
        GameEntity.findById(code) != null
    }
): D20Repository {
    override fun addGame(form: GameForm): Game = transaction {
        val game = GameEntity.new(generateCodes.next()) {
            name = form.name
        }
        val dm = form.dm.copy(gameCode = game.code.value)
        createPlayerInTransaction(dm).game = game
        game.toModel()
    }

    override fun deleteGame(code: String?) = transaction {
        val game = GameEntity.findById(code?.uppercase() ?: throw InvalidGameCode(null))
        game?.delete() ?: throw InvalidGameCode(code)
    }

    override fun getGameByCode(code: String?): Game = transaction {
        GameEntity
            .findById(code?.uppercase() ?: throw InvalidGameCode(null))
            ?.toModel()
            ?: throw InvalidGameCode(code)
    }
    override fun addPlayerToGame(playerId: Int?, gameCode: String?) = transaction {
        val playerEntity = PlayerEntity
            .findById(playerId ?: throw InvalidPlayerId(null))
            ?: throw InvalidPlayerId(playerId)
        playerEntity.game = GameEntity
            .findById(gameCode?.uppercase() ?: throw InvalidGameCode(null))
            ?: throw InvalidGameCode(gameCode)
    }

    override fun hasGameWithCode(code: String?): Boolean = transaction {
        GameEntity.findById(code?.uppercase() ?: return@transaction false) != null
    }

    override fun createPlayer(form: PlayerForm): Player = transaction {
       createPlayerInTransaction(form).toModel()
    }

    private fun createPlayerInTransaction(form: PlayerForm): PlayerEntity = PlayerEntity.new {
        name = form.name
        isDM = form.isDM
        form.gameCode?.let { code ->
            GameEntity.findById(code.uppercase())?.let { entity ->
                game = entity
            } ?: throw InvalidGameCode(code)
        } ?: throw InvalidGameCode(null)
    }

    override fun deletePlayer(id: Int?) = transaction {
        val player = PlayerEntity.findById(id ?: throw InvalidPlayerId(null))
        player?.delete() ?: throw InvalidPlayerId(id)
    }

    override fun getPlayer(id: Int?): Player = transaction {
        PlayerEntity
            .findById(id ?: throw InvalidPlayerId(null))
            ?.toModel()
            ?: throw InvalidPlayerId(id)
    }

    override fun hasPlayer(id: Int?): Boolean = transaction {
        PlayerEntity.findById(id ?: return@transaction false) != null
    }
}