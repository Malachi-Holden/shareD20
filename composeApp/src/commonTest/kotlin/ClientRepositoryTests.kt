import com.holden.DMForm
import com.holden.Game
import com.holden.GameForm
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientRepositoryTests {
    lateinit var mockD20Client: MockD20Client
    @BeforeTest
    fun setup() {
        mockD20Client = MockD20Client()
    }

    @Test
    fun `addGame should correctly add a game to the database`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val response = mockD20Client.httpClient.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(form)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val gameFromServer = response.body<Game>()
        val game = mockD20Client.serverRepository.getGameByCode(gameFromServer.code)
        assertEquals(game, gameFromServer)
    }
}