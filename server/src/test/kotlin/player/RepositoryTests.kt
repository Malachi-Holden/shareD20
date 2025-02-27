package player

import MockGenerator
import com.holden.*
import com.holden.dieRolls.DieRollsTable
import com.holden.dm.DMEntity
import com.holden.dm.DMForm
import com.holden.dm.DMsTable
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import com.holden.player.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var playersRepository: CrdRepository<Int, PlayerForm, Player>

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite { PlayersPostgresRepository() }
        playersRepository = get()
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }


    @Test
    fun `creating a player should correctly add the player to the specified game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        assertEquals(0, newGame.players.count())
        val player = playersRepository.create(PlayerForm("john", newGame.code.value))
        assertEquals(1, newGame.players.count())
        assertEquals(player, newGame.players.first().toModel())
    }

    @Test
    fun `getPlayer should return the correct player`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val player = PlayerEntity.new {
            name = "john"
            game = newGame
        }
        val playerFromRepo = playersRepository.read(player.id.value)
        assertEquals(player.toModel(), playerFromRepo)
    }

    @Test
    fun `getplayer should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.read(666)
        }
    }

    @Test
    fun `deletePlayer should delete correct player`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val player = PlayerEntity.new {
            name = "john"
            game = newGame
        }
        val playerId = player.id.value
        assertEquals(newGame.players.first().toModel(), player.toModel())
        playersRepository.delete(playerId)
        assertEquals(0, newGame.players.count())
        assertNull(PlayerEntity.findById(playerId))
    }

    @Test
    fun `deletePlayer should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.delete(666)
        }
    }
}