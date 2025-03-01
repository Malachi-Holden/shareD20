package game

import com.holden.D20Repository
import com.holden.MockD20Repository
import com.holden.dm.DMForm
import com.holden.game.Game
import com.holden.game.GameForm
import com.holden.hasDataWithId
import d20TestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.*

class RoutesTests: KoinTest {
    lateinit var repository: D20Repository
    lateinit var testDM: DMForm

    @BeforeTest
    fun setup() {
        val routesTestModule = module {
            single<D20Repository> { MockD20Repository() }
        }
        startKoin {
            modules(routesTestModule)
        }
        repository = get()
        testDM = DMForm("jack")
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }


    @Test
    fun `post game should create game`() = d20TestApplication(repository) { client ->
        val response = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(GameForm(name = "hello world", dm = testDM))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val game = response.body<Game>()
        val gameFromRepo = repository.gamesRepository.retrieve(game.code)
        assertNotNull(gameFromRepo)
        assertEquals(game, gameFromRepo)
    }

    @Test
    fun `get game should return the correct game`() = d20TestApplication(repository) { client ->
        val game = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
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
        val game1 = repository.gamesRepository.create(GameForm(name = "hello world", dm = testDM))
        val game2 = repository.gamesRepository.create(GameForm(name = "goodbye world", dm = testDM))
        assert(repository.gamesRepository.hasDataWithId(game1.code))
        assert(repository.gamesRepository.hasDataWithId(game2.code))
        val dmId = repository.gamesRepository.retrieve(game1.code).dm.id
        val response = client.delete("/games/${game1.code}")
        assert(response.status.isSuccess())
        assertFalse(repository.gamesRepository.hasDataWithId(game1.code))
        assertFalse(repository.dmsRepository.hasDataWithId(dmId), "Deleting the game should delete the associated DM")
        assert(repository.gamesRepository.hasDataWithId(game2.code))
    }

    @Test
    fun `delete game should fail if code is bad`() = d20TestApplication(repository) { client ->
        val response = client.delete("/games/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidGameCode", response.bodyAsText())
    }
}