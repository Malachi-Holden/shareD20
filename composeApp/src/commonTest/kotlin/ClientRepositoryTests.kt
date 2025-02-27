import com.holden.*
import com.holden.games.GameForm
import com.holden.dms.DMForm
import io.ktor.client.*
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
    lateinit var clientRepository: D20RepositoryOld

    @BeforeTest
    fun setup() {
        val composeTestModule = module {
            single <D20Repository> (named("server")){ MockD20Repository() }
            single <HttpClient> { mockHttpClient(get(named("server"))) }
            single <D20RepositoryOld> (named("client")){ ClientRepository() }
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
        val game = serverRepository.gamesRepository.read(gameFromServer.code)
        assertEquals(game, gameFromServer)
    }

    @Test
    fun `getgame should get an existing game`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = serverRepository.gamesRepository.create(form)
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
        val game = serverRepository.gamesRepository.create(form)
        clientRepository.deleteGame(game.code)
        assertFalse(serverRepository.gamesRepository.hasDataWithId(game.code))
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