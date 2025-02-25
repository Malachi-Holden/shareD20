package games

import com.holden.*
import com.holden.games.GameEntity
import com.holden.games.GamesTable
import com.holden.games.toModel
import com.holden.generateSequentialGameCodes
import com.holden.players.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import runTransactionTest
import kotlin.test.*

class RepositoryTests {

    lateinit var repository: D20Repository
    lateinit var testDM: DMForm

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(GamesTable)
            SchemaUtils.create(PlayersTable)
            SchemaUtils.create(DMTable)
        }
        repository = PostgresD20Repository(generateCodes = generateSequentialGameCodes())
        testDM = DMForm("jack")
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(PlayersTable)
            SchemaUtils.drop(DMTable)
            SchemaUtils.drop(GamesTable)
        }
    }

    @Test
    fun `addgame should create a game with the correct attributes`() = runTransactionTest {
        val form = GameForm("Hello world", testDM)
        val game = repository.addGame(form)
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
        val gameFromRepo = repository.getGameByCode("00000000")
        assertEquals(newGame.toModel(), gameFromRepo)
    }

    @Test
    fun `getGame should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            repository.getGameByCode("666")
        }
        assertFailsWith<InvalidGameCode> {
            repository.getGameByCode(null)
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

        assert(repository.hasGameWithCode("00000000"))
        repository.deleteGame("00000000")
        assertFalse(repository.hasGameWithCode("00000000"))
    }

    @Test
    fun `deletegame should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            repository.deleteGame("666")
        }
        assertFailsWith<InvalidGameCode> {
            repository.deleteGame(null)
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
        repository.deleteGame(newGame.code.value)
        transaction {
            assertNull(DMEntity.findById(dm.id))
            assertNull(PlayerEntity.findById(player1.id))
            assertNull(PlayerEntity.findById(player2.id))
        }
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
        val player = repository.createPlayer(PlayerForm("john", newGame.code.value))
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
        val playerFromRepo = repository.getPlayer(player.id.value)
        assertEquals(player.toModel(), playerFromRepo)
    }

    @Test
    fun `getplayer should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            repository.getPlayer(666)
        }
        assertFailsWith<InvalidPlayerId> {
            repository.getPlayer(null)
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
        repository.deletePlayer(playerId)
        assertEquals(0, newGame.players.count())
        assertNull(PlayerEntity.findById(playerId))
    }

    @Test
    fun `deletePlayer should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            repository.deletePlayer(666)
        }
        assertFailsWith<InvalidPlayerId> {
            repository.deletePlayer(null)
        }
    }

    @Test
    fun `getDM should return correctDM`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dm = DMEntity.new {
            name = "Jack"
            game = newGame
        }
        assertEquals(dm.toModel(), repository.getDM(dm.id.value))
    }

    @Test
    fun `getDM should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidDMId> {
            repository.getDM(666)
        }
        assertFailsWith<InvalidDMId> {
            repository.getDM(null)
        }
    }
}