package player

import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.PlayersRepository
import com.holden.dm.DMEntity
import com.holden.game.GameEntity
import com.holden.player.PlayerEntity
import com.holden.player.PlayerForm
import com.holden.player.PlayersPostgresRepository
import com.holden.player.toModel
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
        assertNotNull(PlayerEntity.findById(player.id))
        assertEquals(1, newGame.players.count())
        assertEquals(player, newGame.players.first().toModel())
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
    fun `read player should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.read(666)
        }
    }

    @Test
    fun `delete Player should delete correct player`() = runTransactionTest {
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
    fun `delete Player should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.delete(666)
        }
    }
}