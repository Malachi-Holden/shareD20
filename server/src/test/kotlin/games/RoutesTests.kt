package games

import com.holden.*
import d20TestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.*

class RoutesTests {
    lateinit var repository: D20Repository
    lateinit var testDM: DMForm

    @BeforeTest
    fun setup() {
        repository = D20TestRepository()
        testDM = DMForm("jack")
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
        assertEquals("jack", game.dm.name)
        assertEquals("00000000", game.dm.gameCode)
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
        assertEquals("jack", gameFromServer.dm.name)
    }

    @Test
    fun `delete game should remove the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(GameForm(name = "hello world", dm = testDM))
        repository.addGame(GameForm(name = "goodbye world", dm = testDM))
        assert(repository.hasGameWithCode("00000000"))
        assert(repository.hasGameWithCode("00000001"))
        val dmId = repository.getGameByCode("00000000").dm.id
        val response = client.delete("/games/00000000")
        assert(response.status.isSuccess())
        assertFalse(repository.hasGameWithCode("00000000"))
        assertFalse(repository.hasDM(dmId), "Deleting the game should delete the associated DM")
        assert(repository.hasGameWithCode("00000001"))
    }

    @Test
    fun `post player should add the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.addGame(GameForm("testgame", testDM))
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", testGame.code))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val player = response.body<Player>()
        assertEquals("Jane", player.name)
        val gameFromRepo = repository.getPlayer(player.id)
        assertEquals(player.name, gameFromRepo.name)
    }

    @Test
    fun `post player should add the player to the game`() = d20TestApplication(repository) { client ->
        val code = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(GameForm(name = "hello world", dm = testDM))
        }.body<Game>().code
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", code))
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
            setBody(PlayerForm("Jane", badCode))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        assertEquals("No game found with code 66666666", response.bodyAsText())
    }

    @Test
    fun `get player should return the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.addGame(GameForm(name = "hello world", testDM))
        val player = repository.createPlayer(PlayerForm("new player", testGame.code))
        val response = client.get("/players/${player.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val gottenPlayer = response.body<Player>()
        assertEquals(player, gottenPlayer)
    }

    @Test
    fun `get DM should return the correct dm`() = d20TestApplication(repository) { client ->
        val testGame = repository.addGame(GameForm(name = "hello world", testDM))
        val response = client.get("/dms/${testGame.dm.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val gottenDM = response.body<DM>()
        assertEquals(testGame.dm, gottenDM)
    }
}