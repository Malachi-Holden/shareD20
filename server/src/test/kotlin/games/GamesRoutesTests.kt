package games

import D20TestRepository
import com.holden.D20Repository
import com.holden.Game
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
            setBody(Game(name = "hello world"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val game = response.body<Game>()
        assertEquals(Game(name = "hello world", id = "00000000"), game)
        val gameFromRepo = repository.getGame(game.id)
        assertNotNull(gameFromRepo)
        assertEquals(game, gameFromRepo)
    }

    @Test
    fun `get should return the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(Game(name = "hello world"))
        val response = client.get("/games/00000000")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(response.body(), Game(name = "hello world", id = "00000000"))
    }

    @Test
    fun `delete should remove the correct game`() = d20TestApplication(repository) { client ->
        repository.addGame(Game(name = "hello world"))
        repository.addGame(Game(name = "goodbye world"))
        assert(repository.hasGame("00000000"))
        assert(repository.hasGame("00000001"))
        val response = client.delete("/games/00000000")
        assert(response.status.isSuccess())
        assertFalse(repository.hasGame("00000000"))
        assert(repository.hasGame("00000001"))
    }
}