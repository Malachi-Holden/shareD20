package dieRoll

import com.holden.D20Repository
import com.holden.MockD20Repository
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.dm.DMForm
import com.holden.game.GameForm
import com.holden.hasDataWithId
import com.holden.player.PlayerForm
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
    fun `post dieRoll should correctly create dieroll`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
        val player = repository.playersRepository.create(PlayerForm("new player", testGame.code))
        val response = client.post("/dieRolls") {
            contentType(ContentType.Application.Json)
            setBody(DieRollForm(
                testGame.code,
                player.id,
                20,
                DieRollVisibility.All,
                false
            ))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val gottenDieRoll: DieRoll = response.body()
        val rollFromRepo = repository.dieRollsRepository.retrieve(gottenDieRoll.id)
        assertEquals(rollFromRepo, gottenDieRoll)
    }

    @Test
    fun `get dieRoll should return correct dieroll`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
        val player = repository.playersRepository.create(PlayerForm("new player", testGame.code))
        val dieRoll = repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.All,
            false
        ))
        val response = client.get("/dieRolls/${dieRoll.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val gottenDieRoll: DieRoll = response.body()
        assertEquals(dieRoll, gottenDieRoll)
    }

    @Test
    fun `get dieRoll should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.get("/dieRolls/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidDieRollId", response.bodyAsText())
    }

    @Test
    fun `delete dieRoll should correctly delete dieroll`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
        val player = repository.playersRepository.create(PlayerForm("new player", testGame.code))
        val dieRoll = repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.All,
            false
        ))
        assertTrue(repository.dieRollsRepository.hasDataWithId(dieRoll.id))
        val response = client.delete("/dieRolls/${dieRoll.id}")
        assertTrue(response.status.isSuccess())
        assertFalse(repository.dieRollsRepository.hasDataWithId(dieRoll.id))
    }

    @Test
    fun `delete dieroll should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.delete("/dieRolls/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidDieRollId", response.bodyAsText())
    }
}