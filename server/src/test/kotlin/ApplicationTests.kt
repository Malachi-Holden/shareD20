import com.holden.D20Repository
import com.holden.MockD20Repository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class ApplicationTests {
    lateinit var repository: D20Repository

    @BeforeTest
    fun setup() {
        repository = MockD20Repository()
    }

    @Test
    fun `root url should return welcome message`() = d20TestApplication(repository) {
        val response = client.get("/")
        assertEquals(200, response.status.value)
        assertEquals("Welcome to shareD20", response.bodyAsText())
    }
}