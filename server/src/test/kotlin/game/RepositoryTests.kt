package game

import MockGenerator
import com.holden.*
import com.holden.dieRolls.DieRollsTable
import com.holden.dm.*
import com.holden.game.*
import com.holden.player.Player
import com.holden.player.PlayerEntity
import com.holden.player.PlayersTable
import com.holden.player.toModel
import kotlinx.coroutines.test.runTest
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
    lateinit var gamesRepository: CrdRepository<String, GameForm, Game>

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite { GamesPostgresRepository() }
        gamesRepository = get()
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }

    @Test
    fun `addgame should create a game with the correct attributes`() = runTransactionTest {
        val form = GameForm("Hello world", DMForm("jack"))
        val game = gamesRepository.create(form)
        assertEquals(form.name, game.name)
        assertEquals(form.dm.name, game.dm.name)
        val gottenGame = GameEntity.findById(game.code)?.toModel()
        assertEquals(game, gottenGame)
    }

    @Test
    fun `getGame should get the correct game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val gameFromRepo = gamesRepository.read("00000000")
        assertEquals(newGame.toModel(), gameFromRepo)
    }

    @Test
    fun `getGame should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            gamesRepository.read("666")
        }
    }

    @Test
    fun `deletegame should remove the game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }

        assert(gamesRepository.hasDataWithId("00000000"))
        gamesRepository.delete("00000000")
        assertFalse(gamesRepository.hasDataWithId("00000000"))
    }

    @Test
    fun `deletegame should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            gamesRepository.delete("666")
        }
    }

    @Test
    fun `deletegame should delete all its players and its dm`() = runTest {
        lateinit var newGame: GameEntity
        lateinit var dm: DM
        lateinit var player1: Player
        lateinit var player2: Player
        transaction {
            newGame = GameEntity.new("00000000") {
                name = "Hello world"
            }
            dm = DMEntity.new {
                name = "Jack"
                game = newGame
            }.toModel()
            player1 = PlayerEntity.new {
                name = "john"
                game = newGame
            }.toModel()
            player2 = PlayerEntity.new {
                name = "jane"
                game = newGame
            }.toModel()
        }
        gamesRepository.delete(newGame.code.value)
        transaction {
            assertNull(DMEntity.findById(dm.id))
            assertNull(PlayerEntity.findById(player1.id))
            assertNull(PlayerEntity.findById(player2.id))
        }
    }
}