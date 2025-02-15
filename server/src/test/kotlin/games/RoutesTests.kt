package games

import D20TestRepository
import com.holden.*
import d20TestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.*

class RoutesTests {
    lateinit var repository: D20Repository
    lateinit var testDM: Player

    @BeforeTest
    fun setup() {
        repository = D20TestRepository()
        testDM = repository.createPlayer(PlayerForm("jack", true, null))
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
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", false, null))
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
        val player = response.body<Player>()
        val game = repository.getGameByCode(code)
        assertEquals(game?.players?.last()?.name, player.name)
        assertEquals(game?.code, player.gameCode)
    }
}