package games

import com.holden.*
import com.holden.games.GamesTable
import com.holden.players.PlayersTable
import com.holden.generateSequentialGameCodes
import d20TestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class RoutesTests {
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
    fun `post game should create game`() = d20TestApplication(repository) { client ->
        val response = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(GameForm(name = "hello world", dm = testDM))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val game = response.body<Game>()
        assertEquals("hello world" to "00000000", game.name to game.code)
        val gameFromRepo = repository.getGameByCode(game.code)
        assertNotNull(gameFromRepo)
        assertEquals(game, gameFromRepo)
    }

    @Test
    fun `get game should return the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(GameForm(name = "hello world", testDM))
        val response = client.get("/games/00000000")
        assertEquals(HttpStatusCode.OK, response.status)
        val gameFromServer: Game = response.body()
        assertEquals("hello world" to "00000000", gameFromServer.name to gameFromServer.code)
    }

    @Test
    fun `delete game should remove the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(GameForm(name = "hello world", dm = testDM))
        repository.addGame(GameForm(name = "goodbye world", dm = testDM))
        assert(repository.hasGameWithCode("00000000"))
        assert(repository.hasGameWithCode("00000001"))
        val response = client.delete("/games/00000000")
        assert(response.status.isSuccess())
        assertFalse(repository.hasGameWithCode("00000000"))
        assert(repository.hasGameWithCode("00000001"))
    }

    @Test
    fun `post player should add the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.addGame(GameForm("testgame", testDM))
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", false, testGame.code))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val player = response.body<Player>()
        assertEquals("Jane", player.name)
        val gameFromRepo = repository.getPlayer(player.id)
        assertEquals(player.name, gameFromRepo?.name)
    }

    @Test
    fun `post player should add the player to the game`() = d20TestApplication(repository) { client ->
        val code = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(GameForm(name = "hello world", dm = testDM))
        }.body<Game>().code
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", false, code))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val player = response.body<Player>()
        val game = repository.getGameByCode(code)
        assertEquals(game.players.last().name, player.name)
        assertEquals(game.code, player.gameCode)
    }

    @Test
    fun `post player should return 404 if the game code doesn't exist`() = d20TestApplication(repository) { client ->
        val badCode = "66666666"
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", false, badCode))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        assertEquals("No game found with code 66666666", response.bodyAsText())
    }
}