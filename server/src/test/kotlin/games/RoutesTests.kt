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
        repository = MockD20Repository()
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
        val gameFromRepo = repository.getGameByCode(game.code)
        assertNotNull(gameFromRepo)
        assertEquals(game, gameFromRepo)
    }

    @Test
    fun `get game should return the correct game`() = d20TestApplication(repository) { client ->
        val game = repository.addGame(GameForm(name = "hello world", testDM))
        val response = client.get("/games/${game.code}")
        assertEquals(HttpStatusCode.OK, response.status)
        val gameFromServer: Game = response.body()
        assertEquals(game, gameFromServer)
        assertEquals("jack", gameFromServer.dm.name)
    }

    @Test
    fun `getGame should fail if code is bad`() = d20TestApplication(repository) { client ->
        val response = client.get("/games/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidGameCode", response.bodyAsText())
    }

    @Test
    fun `delete game should remove the correct game`() = d20TestApplication(repository) { client ->
        val game1 = repository.addGame(GameForm(name = "hello world", dm = testDM))
        val game2 = repository.addGame(GameForm(name = "goodbye world", dm = testDM))
        assert(repository.hasGameWithCode(game1.code))
        assert(repository.hasGameWithCode(game2.code))
        val dmId = repository.getGameByCode(game1.code).dm.id
        val response = client.delete("/games/${game1.code}")
        assert(response.status.isSuccess())
        assertFalse(repository.hasGameWithCode(game1.code))
        assertFalse(repository.hasDM(dmId), "Deleting the game should delete the associated DM")
        assert(repository.hasGameWithCode(game2.code))
    }

    @Test
    fun `delete game should fail if code is bad`() = d20TestApplication(repository) { client ->
        val response = client.delete("/games/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidGameCode", response.bodyAsText())
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

        assertEquals("InvalidGameCode", response.bodyAsText())
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
    fun `get player should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.get("/players/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidPlayerId", response.bodyAsText())
    }

    @Test
    fun `delete Player should delete the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.addGame(GameForm("testgame", testDM))
        val player1 = repository.createPlayer(PlayerForm("Jane", testGame.code))
        val player2 = repository.createPlayer(PlayerForm("Jill", testGame.code))
        assert(repository.hasPlayer(player1.id))
        assert(repository.hasPlayer(player2.id))
        val response = client.delete("/players/${player1.id}")
        assert(response.status.isSuccess())
        assertFalse(repository.hasPlayer(player1.id))
        assert(repository.hasPlayer(player2.id))
    }

    @Test
    fun `delete player should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.delete("/players/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidPlayerId", response.bodyAsText())
    }

    @Test
    fun `get DM should return the correct dm`() = d20TestApplication(repository) { client ->
        val testGame = repository.addGame(GameForm(name = "hello world", testDM))
        val response = client.get("/dms/${testGame.dm.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val gottenDM = response.body<DM>()
        assertEquals(testGame.dm, gottenDM)
    }

    @Test
    fun `getDM should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.get("/dms/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidDMId", response.bodyAsText())
    }
}