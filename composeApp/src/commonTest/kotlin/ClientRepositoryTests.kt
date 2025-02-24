import com.holden.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ClientRepositoryTests {
    lateinit var mockD20Client: MockD20Client
    lateinit var clientRepository: D20Repository
    @BeforeTest
    fun setup() {
        mockD20Client = MockD20Client()
        clientRepository = ClientRepository(mockD20Client.httpClient)
    }

    @Test
    fun `addGame should correctly add a game to the database`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val gameFromServer = clientRepository.addGame(form)
        val game = mockD20Client.serverRepository.getGameByCode(gameFromServer.code)
        assertEquals(game, gameFromServer)
    }

    @Test
    fun `getgame should get an existing game`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = mockD20Client.serverRepository.addGame(form)
        val gameFromServer = clientRepository.getGameByCode(game.code)
        assertEquals(game, gameFromServer)
    }

    @Test
    fun `getgame should fail if game does not exist`() = runTest {
        assertFailsWith<InvalidGameCode> {
            clientRepository.getGameByCode("99")
        }
        assertFailsWith<InvalidGameCode> {
            clientRepository.getGameByCode(null)
        }
    }

    @Test
    fun `deleteGame should successfully delete game`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = mockD20Client.serverRepository.addGame(form)
        clientRepository.deleteGame(game.code)
        assertFalse(mockD20Client.serverRepository.hasGameWithCode(game.code))
    }

    @Test
    fun `deletegame shuld fail if game does not exist`() = runTest {
        assertFailsWith<InvalidGameCode> {
            clientRepository.deleteGame("99")
        }
        assertFailsWith<InvalidGameCode> {
            clientRepository.deleteGame(null)
        }
    }
}