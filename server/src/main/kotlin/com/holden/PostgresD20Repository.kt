package com.holden

import com.holden.games.GameEntity
import com.holden.games.toModel
import com.holden.players.DMEntity
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
        val code = generateCodes.next()
        val newGame = GameEntity.new(code) {
            name = form.name
        }
        DMEntity.new {
            name = form.dm.name
            game = newGame
        }
        newGame.toModel()
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
//    override fun addPlayerToGame(playerId: Int?, gameCode: String?) = transaction {
//        val playerEntity = PlayerEntity
//            .findById(playerId ?: throw InvalidPlayerId(null))
//            ?: throw InvalidPlayerId(playerId)
//        playerEntity.game = GameEntity
//            .findById(gameCode?.uppercase() ?: throw InvalidGameCode(null))
//            ?: throw InvalidGameCode(gameCode)
//    }

    override fun hasGameWithCode(code: String?): Boolean = transaction {
        GameEntity.findById(code?.uppercase() ?: return@transaction false) != null
    }

    override fun createPlayer(form: PlayerForm): Player = transaction {
        PlayerEntity.new {
            name = form.name
            game = GameEntity
                .findById(form.gameCode.uppercase())
                ?: throw InvalidGameCode(form.gameCode)
        }.toModel()
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

    override fun getDM(id: Int?): DM = transaction {
        DMEntity
            .findById(id ?: throw InvalidDMId(null))
            ?.toModel()
            ?: throw InvalidDMId(id)
    }

    override fun hasDM(id: Int?): Boolean = transaction {
        DMEntity.findById(id ?: return@transaction false) != null
    }
}