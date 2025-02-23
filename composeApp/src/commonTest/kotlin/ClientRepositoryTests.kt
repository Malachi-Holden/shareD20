import com.holden.ClientRepository
import io.ktor.client.*
import kotlin.test.BeforeTest

class ClientRepositoryTests {
    val testClient = HttpClient(MockClientEngine)
    lateinit var testRepository: ClientRepository
    @BeforeTest
    fun setup() {
        testRepository = ClientRepository(testClient)
    }


}