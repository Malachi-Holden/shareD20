package com.holden

import com.holden.dms.DM
import com.holden.games.Game
import com.holden.games.GameEntity
import com.holden.games.GameForm
import com.holden.games.toModel
import com.holden.players.*
import com.holden.util.uniqueRandomStringIterator
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

val GAME_ID_LENGTH = 8 // in the future this could be set by the administrator

object StandardGenerator: GenerateCodes {
    val generator = uniqueRandomStringIterator(GAME_ID_LENGTH) { code ->
        GameEntity.findById(code) != null
    }

    override fun next(): String = generator.next()
}

class PostgresD20RepositoryOld: D20RepositoryOld, KoinComponent {
    private val generateCodes: GenerateCodes by inject()
    override suspend fun addGame(form: GameForm): Game = transaction {
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

    override suspend fun deleteGame(code: String?) = transaction {
        val game = GameEntity.findById(code?.uppercase() ?: throw InvalidGameCode(null))
        game?.delete() ?: throw InvalidGameCode(code)
    }

    override suspend fun getGameByCode(code: String?): Game = transaction {
        GameEntity
            .findById(code?.uppercase() ?: throw InvalidGameCode(null))
            ?.toModel()
            ?: throw InvalidGameCode(code)
    }

    override suspend fun hasGameWithCode(code: String?): Boolean = transaction {
        GameEntity.findById(code?.uppercase() ?: return@transaction false) != null
    }

    override suspend fun createPlayer(form: PlayerForm): Player = transaction {
        PlayerEntity.new {
            name = form.name
            game = GameEntity
                .findById(form.gameCode.uppercase())
                ?: throw InvalidGameCode(form.gameCode)
        }.toModel()
    }

    override suspend fun deletePlayer(id: Int?) = transaction {
        val player = PlayerEntity.findById(id ?: throw InvalidPlayerId(null))
        player?.delete() ?: throw InvalidPlayerId(id)
    }

    override suspend fun getPlayer(id: Int?): Player = transaction {
        PlayerEntity
            .findById(id ?: throw InvalidPlayerId(null))
            ?.toModel()
            ?: throw InvalidPlayerId(id)
    }

    override suspend fun hasPlayer(id: Int?): Boolean = transaction {
        PlayerEntity.findById(id ?: return@transaction false) != null
    }

    override suspend fun getDM(id: Int?): DM = transaction {
        DMEntity
            .findById(id ?: throw InvalidDMId(null))
            ?.toModel()
            ?: throw InvalidDMId(id)
    }

    override suspend fun hasDM(id: Int?): Boolean = transaction {
        DMEntity.findById(id ?: return@transaction false) != null
    }
}