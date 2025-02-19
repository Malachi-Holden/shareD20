package games

import com.holden.*
import com.holden.games.GamesTable
import com.holden.players.PlayersTable
import com.holden.generateSequentialGameCodes
import com.holden.players.DMTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
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
    fun `addgame should create a game with the correct attributes`() {
        val game = repository.addGame(GameForm("Hello world", testDM))
        assertEquals("00000000" to "Hello world", game.code to game.name)
        val gottenGame = repository.getGameByCode("00000000")
        assertEquals(game, gottenGame)
    }

    @Test
    fun `deletegame should remove the game`() {
        repository.addGame(GameForm("Hello world", testDM))
        assert(repository.hasGameWithCode("00000000"))
        repository.deleteGame("00000000")
        assertFalse(repository.hasGameWithCode("00000000"))
    }

    @Test
    fun `creating a player should correctly add the player to the specified game`() {
        var game = repository.addGame(GameForm("Hello world", testDM))
        assertEquals(0, game.players.size)
        repository.createPlayer(PlayerForm("john", game.code))
//        repository.addPlayerToGame(player.id, game.code)
        game = repository.getGameByCode(game.code)
        assertEquals(1, game.players.size)
        assertEquals("john", game.players.last().name)
    }

    @Test
    fun `deletegame should delete all its players and its dm`() {
        val game = repository.addGame(GameForm("Hello world", testDM))
        assert(repository.hasGameWithCode("00000000"))
        val player1 = repository.createPlayer(PlayerForm("john", game.code))
        val player2 = repository.createPlayer(PlayerForm("jane", game.code))
        val dm = game.dm
        repository.deleteGame(game.code)
        assertFalse(repository.hasDM(dm.id))
        assertFalse(repository.hasPlayer(player1.id))
        assertFalse(repository.hasPlayer(player2.id))
    }
}