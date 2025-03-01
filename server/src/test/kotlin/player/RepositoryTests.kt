package player

import assertContentEqualsOrderless
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollVisibility
import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.toModel
import com.holden.dm.DMEntity
import com.holden.game.Game
import com.holden.game.GameEntity
import com.holden.player.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var playersRepository: PlayersRepository
    lateinit var testGame: GameEntity
    lateinit var testDmPlayer: PlayerEntity
    lateinit var testDM: DMEntity


    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite<PlayersRepository> { PlayersPostgresRepository() }
        playersRepository = get()
        transaction {
            testGame = GameEntity.new("00000000") {
                name = "Hello world"
            }
            testDmPlayer = PlayerEntity.new {
                name = "Jack"
                game = testGame
            }
            testDM = DMEntity.new {
                name = "Jack"
                player = testDmPlayer
                game = testGame
            }
        }

    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }

    @Test
    fun `creating a player should correctly add the player to the specified game`() = runTransactionTest {
        assertEquals(1, testGame.players.count())
        val player = playersRepository.create(PlayerForm("john", testGame.code.value))
        assertNotNull(PlayerEntity.findById(player.id))
        assertEquals(2, testGame.players.count())
        assertContains(testGame.players.map { it.toModel() }, player)
    }

    @Test
    fun `create player should fail if gamecode is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            playersRepository.create(PlayerForm("John", "666"))
        }
    }

    @Test
    fun `retrieve Player should return the correct player`() = runTransactionTest {
        val player = PlayerEntity.new {
            name = "john"
            game = testGame
        }
        val playerFromRepo = playersRepository.retrieve(player.id.value)
        assertEquals(player.toModel(), playerFromRepo)
    }

    @Test
    fun `retrieve player should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.retrieve(666)
        }
    }

    @Test
    fun `delete Player should delete correct player`() = runTransactionTest {
        val player = PlayerEntity.new {
            name = "john"
            game = testGame
        }
        val playerId = player.id.value
        assertContains(testGame.players.map { it.toModel() }, player.toModel())
        playersRepository.delete(playerId)
        assertEquals(1, testGame.players.count())
        assertNull(PlayerEntity.findById(playerId))
    }

    @Test
    fun `delete Player should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.delete(666)
        }
    }

    @Test
    fun `retrieve dierolls should return correct rolls`() = runTransactionTest {
        val player = PlayerEntity.new {
            name = "john"
            game = testGame
        }
        val roll1 = DieRollEntity.new {
            game = testGame
            rolledBy = player
            value = 20
            visibility = DieRollVisibility.All.ordinal
            fromDM = false
        }.toModel()
        val roll2 = DieRollEntity.new {
            game = testGame
            rolledBy = player
            value = 20
            visibility = DieRollVisibility.All.ordinal
            fromDM = false
        }.toModel()
        val rolls = playersRepository.retreiveDieRolls(player.id.value)
        assertContentEqualsOrderless(listOf(roll1, roll2), rolls)
    }

    @Test
    fun `retrieve dierolls should fail if player id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            playersRepository.retreiveDieRolls(666)
        }
    }
}