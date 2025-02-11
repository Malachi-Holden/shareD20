import com.holden.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.Test
import io.ktor.server.testing.*
import kotlin.test.assertEquals

class ApplicationTests {
    @Test
    fun `root url should return welcome message`() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(200, response.status.value)
        assertEquals("Hello, world!", response.bodyAsText())
    }
}