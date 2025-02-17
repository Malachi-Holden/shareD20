package games

import com.holden.*
import com.holden.games.GamesTable
import com.holden.players.PlayersTable
import com.holden.generateSequentialGameCodes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class RepositoryTests {

    lateinit var repository: D20Repository
    lateinit var testDM: PlayerForm

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(GamesTable)
            SchemaUtils.create(PlayersTable)
        }
        repository = PostgresD20Repository(generateCodes = generateSequentialGameCodes())
        testDM = PlayerForm("jack", true, null)
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(PlayersTable)
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
    fun `adding a player to a game should correctly add the player`() {
        var game = repository.addGame(GameForm("Hello world", testDM))
        assertEquals(1, game.players.size)
        assertEquals("jack", game.players.first().name)
        val player = repository.createPlayer(PlayerForm("john", false, game.code))
        repository.addPlayerToGame(player.id, game.code)
        game = repository.getGameByCode(game.code)
        assertEquals(2, game.players.size)
        assertEquals("john", game.players.last().name)
    }
}