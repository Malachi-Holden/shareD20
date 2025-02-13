package games

import D20TestRepository
import com.holden.D20Repository
import com.holden.Game
import com.holden.GameForm
import d20TestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.*

class GamesRoutesTests {
    lateinit var repository: D20Repository

    @BeforeTest
    fun setup() {
        repository = D20TestRepository()
    }

    @Test
    fun `post should create game`() = d20TestApplication(repository) { client ->
        val response = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(GameForm(name = "hello world"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val game = response.body<Game>()
        assertEquals(Game(name = "hello world", code = "00000000"), game)
        val gameFromRepo = repository.getGameByCode(game.code)
        assertNotNull(gameFromRepo)
        assertEquals(game, gameFromRepo)
    }

    @Test
    fun `get should return the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(GameForm(name = "hello world"))
        val response = client.get("/games/00000000")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(response.body(), Game(name = "hello world", code = "00000000"))
    }

    @Test
    fun `delete should remove the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(GameForm(name = "hello world"))
        repository.addGame(GameForm(name = "goodbye world"))
        assert(repository.hasGameWithCode("00000000"))
        assert(repository.hasGameWithCode("00000001"))
        val response = client.delete("/games/00000000")
        assert(response.status.isSuccess())
        assertFalse(repository.hasGameWithCode("00000000"))
        assert(repository.hasGameWithCode("00000001"))
    }
}