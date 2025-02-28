package dm

import com.holden.D20Repository
import com.holden.InvalidDMId
import com.holden.dm.DMForm
import com.holden.game.GameForm
import kotlinx.coroutines.test.runTest
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import util.RepositoryType
import util.setupRepositoryTestSuite
import util.tearDownRepositoryTestSuite
import kotlin.test.*

class ClientRepositoryTests : KoinTest {
    lateinit var serverRepository: D20Repository
    lateinit var clientRepository: D20Repository

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite()
        serverRepository = get(named(RepositoryType.Server))
        clientRepository = get(named(RepositoryType.Client))
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }

    @Test
    fun `create game should also create dm`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = clientRepository.gamesRepository.create(form)
        val dm = game.dm
        val dmFromServer = serverRepository.dmsRepository.read(dm.id)
        assertEquals(dmFromServer, dm)
    }

    @Test
    fun `read dm should get existing dm`() = runTest {
        val form = GameForm("Jack's game", DMForm("Jack"))
        val game = serverRepository.gamesRepository.create(form)
        val dmFromServer = game.dm
        val dm = clientRepository.dmsRepository.read(dmFromServer.id)
        assertEquals(dmFromServer, dm)
    }

    @Test
    fun `read dm should fail if id is bad`() = runTest {
        assertFailsWith<InvalidDMId> {
            clientRepository.dmsRepository.read(666)
        }
    }
}