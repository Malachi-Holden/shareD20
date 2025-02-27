package dm

import com.holden.D20Repository
import com.holden.MockD20Repository
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.game.GameForm
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun `get DM should return the correct dm`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
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