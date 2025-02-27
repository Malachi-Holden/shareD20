package com.holden

import com.holden.dieRolls.DieRollsPostgresRepository
import com.holden.dms.DM
import com.holden.dms.DMForm
import com.holden.dms.DMsPostgresRepository
import com.holden.games.GameEntity
import com.holden.games.GamesPostgresRepository
import com.holden.players.Player
import com.holden.players.PlayerForm
import com.holden.players.PlayersPostgresRepository
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