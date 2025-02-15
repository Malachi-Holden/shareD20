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
        GameEntity.new(generateCodes.next()) {
            name = form.name
        }
            .also {
                PlayerEntity.findById(form.dm.id)?.game = it
            }
            .toModel()
    }

    override fun deleteGame(code: String?): Boolean = transaction {
        val game = GameEntity.findById(code ?: return@transaction false)
        game?.delete() != null
    }

    override fun getGameByCode(code: String?): Game? = transaction {
        GameEntity
            .findById(code ?: return@transaction null)
            ?.toModel()

    }
    override fun addPlayerToGame(playerId: Int?, gameCode: String?): Boolean = transaction {
        val playerEntity = PlayerEntity
            .findById(playerId ?: return@transaction false)
            ?: return@transaction false
        playerEntity.game = GameEntity
            .findById(gameCode ?: return@transaction false)
            ?: return@transaction false
        true
    }

    override fun hasGameWithCode(code: String?): Boolean = getGameByCode(code) != null

    override fun createPlayer(form: PlayerForm): Player = transaction {
       PlayerEntity.new {
           name = form.name
           isDM = form.isDM
           form.gameCode?.let { code ->
               GameEntity.findById(code)?.let { entity ->
                   game = entity
               }
           }
        }.toModel()
    }

    override fun deletePlayer(id: Int?): Boolean = transaction {
        val player = PlayerEntity.findById(id ?: return@transaction false)
        player?.delete() != null
    }

    override fun getPlayer(id: Int?): Player? = transaction {
        PlayerEntity
            .findById(id ?: return@transaction null)
            ?.toModel()
    }

    override fun hasPlayer(id: Int?): Boolean {
        return getPlayer(id) != null
    }
}