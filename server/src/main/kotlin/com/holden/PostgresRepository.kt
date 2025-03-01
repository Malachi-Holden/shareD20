package com.holden

import com.holden.dieRolls.DieRollsPostgresRepository
import com.holden.dm.DMsPostgresRepository
import com.holden.game.GameEntity
import com.holden.game.GamesPostgresRepository
import com.holden.player.PlayersPostgresRepository
import com.holden.util.uniqueRandomStringIterator
import org.koin.core.component.KoinComponent

val GAME_ID_LENGTH = 8 // in the future this could be set by the administrator

object StandardGenerator: GenerateCodes {
    val generator = uniqueRandomStringIterator(GAME_ID_LENGTH) { code ->
        GameEntity.findById(code) != null
    }

    override fun next(): String = generator.next()
}

class PostgresRepository: D20Repository, KoinComponent {
    override val gamesRepository = GamesPostgresRepository()
    override val playersRepository = PlayersPostgresRepository()
    override val dmsRepository = DMsPostgresRepository()
    override val dieRollsRepository = DieRollsPostgresRepository()
}