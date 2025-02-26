import com.holden.*
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.*

class ClientRepositoryTests: KoinTest {
    lateinit var serverRepository: D20Repository
    lateinit var clientRepository: D20Repository

    @BeforeTest
    fun setup() {
        val composeTestModule = module {
            single <D20Repository> (named("server")){ MockD20Repository() }
            single { mockHttpClient(get(named("server"))) }
            single <D20Repository> (named("client")){ ClientRepository() }
            viewModelOf<D20ViewModel>(constructor = { D20ViewModel() })
        }
        startKoin {
            modules(composeTestModule)
        }
        serverRepository = get(named("server"))
        clientRepository = get(named("client"))
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `addGame should correctly add a game to the database`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val gameFromServer = clientRepository.addGame(form)
        val game = serverRepository.getGameByCode(gameFromServer.code)
        assertEquals(game, gameFromServer)
    }

    @Test
    fun `getgame should get an existing game`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = serverRepository.addGame(form)
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
        val game = serverRepository.addGame(form)
        clientRepository.deleteGame(game.code)
        assertFalse(serverRepository.hasGameWithCode(game.code))
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