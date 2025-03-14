import com.holden.D20Repository
import com.holden.MockD20Repository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTests: KoinTest {
    lateinit var repository: D20Repository

    @BeforeTest
    fun setup() {
        val routesTestModule = module {
            single<D20Repository> { MockD20Repository() }
        }
        startKoin {
            modules(routesTestModule)
        }
        repository = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `root url should return welcome message`() = d20TestApplication(repository) {
        val response = client.get("/")
        assertEquals(200, response.status.value)
        assertEquals("Welcome to shareD20", response.bodyAsText())
    }
}