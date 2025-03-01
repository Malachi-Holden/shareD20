package player

import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dm.DMEntity
import com.holden.game.GameEntity
import com.holden.player.*
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var playersRepository: PlayersRepository

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite<PlayersRepository> { PlayersPostgresRepository() }
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
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            player = dmPlayer
            game = newGame
        }
        assertEquals(1, newGame.players.count())
        val player = playersRepository.create(PlayerForm("john", newGame.code.value))
        assertNotNull(PlayerEntity.findById(player.id))
        assertEquals(2, newGame.players.count())
        assertContains(newGame.players.map { it.toModel() }, player)
    }

    @Test
    fun `create player should fail if gamecode is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            playersRepository.create(PlayerForm("John", "666"))
        }
    }

    @Test
    fun `read Player should return the correct player`() = runTransactionTest {
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
        val player = PlayerEntity.new {
            name = "john"
            game = newGame
        }
        val playerFromRepo = playersRepository.retrieve(player.id.value)
        assertEquals(player.toModel(), playerFromRepo)
    }

    @Test
    fun `read player should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.retrieve(666)
        }
    }

    @Test
    fun `delete Player should delete correct player`() = runTransactionTest {
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
        val player = PlayerEntity.new {
            name = "john"
            game = newGame
        }
        val playerId = player.id.value
        assertContains(newGame.players.map { it.toModel() }, player.toModel())
        playersRepository.delete(playerId)
        assertEquals(1, newGame.players.count())
        assertNull(PlayerEntity.findById(playerId))
    }

    @Test
    fun `delete Player should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.delete(666)
        }
    }

    // todo: test deleting player tied to dm cascades
}