package games

import com.holden.D20Repository
import com.holden.Game
import com.holden.GameForm
import com.holden.PostgresD20Repository
import com.holden.games.GamesTable
import generateSequentialIds
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class RepositoryTests {

    lateinit var repository: D20Repository
    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(GamesTable)
        }
        repository = PostgresD20Repository(generateCodes = generateSequentialIds())
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(GamesTable)
        }
    }

    @Test
    fun `addgame should create a game with the correct attributes`() {
        val game = repository.addGame(GameForm("Hello world"))
        assertEquals(Game("00000000", "Hello world"), game)
        val gottenGame = repository.getGameByCode("00000000")
        assertEquals(game, gottenGame)
    }

    @Test
    fun `deletegame should remove the game`() {
        repository.addGame(GameForm("Hello world"))
        assert(repository.hasGameWithCode("00000000"))
        repository.deleteGame("00000000")
        assertFalse(repository.hasGameWithCode("00000000"))
    }
}