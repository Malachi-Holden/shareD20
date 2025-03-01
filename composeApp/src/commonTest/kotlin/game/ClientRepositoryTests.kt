package game

import com.holden.D20Repository
import com.holden.InvalidGameCode
import com.holden.dm.DMForm
import com.holden.game.GameForm
import com.holden.hasDataWithId
import kotlinx.coroutines.test.runTest
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import util.setupRepositoryTestSuite
import util.tearDownRepositoryTestSuite
import kotlin.test.*

class ClientRepositoryTests: KoinTest {
    lateinit var serverRepository: D20Repository
    lateinit var clientRepository: D20Repository

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite()
        serverRepository = get(named("server"))
        clientRepository = get(named("client"))
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }

    @Test
    fun `create Game should correctly add a game to the database`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val gameFromServer = clientRepository.gamesRepository.create(form)
        val game = serverRepository.gamesRepository.retrieve(gameFromServer.code)
        assertEquals(game, gameFromServer)
    }

    @Test
    fun `read game should get an existing game`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val gameFromServer = serverRepository.gamesRepository.create(form)
        val game = clientRepository.gamesRepository.retrieve(gameFromServer.code)
        assertEquals(gameFromServer, game)
    }

    @Test
    fun `read game should fail if game does not exist`() = runTest {
        assertFailsWith<InvalidGameCode> {
            clientRepository.gamesRepository.retrieve("99")
        }
    }

    @Test
    fun `delete Game should successfully delete game`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = serverRepository.gamesRepository.create(form)
        clientRepository.gamesRepository.delete(game.code)
        assertFalse(serverRepository.gamesRepository.hasDataWithId(game.code))
    }

    @Test
    fun `delete game should fail if game does not exist`() = runTest {
        assertFailsWith<InvalidGameCode> {
            clientRepository.gamesRepository.delete("99")
        }
    }
}