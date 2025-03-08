package game

import assertContentEqualsOrderless
import com.holden.InvalidGameCode
import com.holden.dm.*
import com.holden.game.*
import com.holden.hasDataWithId
import com.holden.player.Player
import com.holden.player.PlayerEntity
import com.holden.player.PlayersTable
import com.holden.player.toModel
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var gamesRepository: GamesRepository

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite<GamesRepository> { GamesPostgresRepository() }
        gamesRepository = get()
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }

    @Test
    fun `create game should create a game with the correct attributes`() = runTransactionTest {
        val form = GameForm("Hello world", DMForm("jack"))
        val game = gamesRepository.create(form)
        val dmPlayer = PlayerEntity.findById(game.dm.playerId)
        assertEquals(form.name, game.name)
        assertEquals(form.dm.name, game.dm.name)
        assertEquals(dmPlayer?.name, form.dm.name)
        val gottenGame = GameEntity.findById(game.code)?.toModel()
        assertEquals(game, gottenGame)
    }

    @Test
    fun `retreive game should get the correct game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        val gameFromRepo = gamesRepository.retrieve("00000000")
        assertEquals(newGame.toModel(), gameFromRepo)
    }

    @Test
    fun `retreive Game should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            gamesRepository.retrieve("666")
        }
    }

    @Test
    fun `delete game should remove the game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            player = dmPlayer
            game = newGame
        }

        assert(gamesRepository.hasDataWithId("00000000"))
        gamesRepository.delete("00000000")
        assertFalse(gamesRepository.hasDataWithId("00000000"))
    }

    @Test
    fun `delete game should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            gamesRepository.delete("666")
        }
    }

    @Test
    fun `delete game should delete all its players and its dm`() = runTest {
        lateinit var newGame: GameEntity
        lateinit var dm: DM
        lateinit var player1: Player
        lateinit var player2: Player
        transaction {
            newGame = GameEntity.new("00000000") {
                name = "Hello world"
            }
            val dmPlayer = PlayerEntity.new {
                name = "Jack"
                game = newGame
            }
            dm = DMEntity.new {
                name = "Jack"
                game = newGame
                player = dmPlayer
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
            assertNull(PlayerEntity.findById(dm.playerId))
            assertNull(PlayerEntity.findById(player1.id))
            assertNull(PlayerEntity.findById(player2.id))
        }
    }

    @Test
    fun `retrieve players should return the correct list of players`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }.toModel()
        val player1 = PlayerEntity.new {
            name = "john"
            game = newGame
        }.toModel()
        val player2 = PlayerEntity.new {
            name = "jane"
            game = newGame
        }.toModel()

        val players = gamesRepository.retreivePlayers(newGame.code.value)

        assertContentEqualsOrderless(players, listOf(player1, player2, dmPlayer.toModel()))
        assertContains(players.map { it.name }, "Jack")
    }

    @Test
    fun `retrieve players should fail if gamecode is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            gamesRepository.retreivePlayers("666")
        }
    }
}