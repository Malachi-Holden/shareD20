package player

import com.holden.D20Repository
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dm.DMForm
import com.holden.game.GameForm
import com.holden.hasDataWithId
import com.holden.player.PlayerForm
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

    suspend fun testGame() = serverRepository.gamesRepository.create(GameForm("The Game", DMForm("John")))

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
    fun `create player should correctly add a player to the database`() = runTest {
        val gameCode = testGame().code
        val form = PlayerForm("Jack", gameCode)
        val player = clientRepository.playersRepository.create(form)
        val playerFromServer = serverRepository.playersRepository.retrieve(player.id)
        assertEquals(playerFromServer, player)
        assertContains(serverRepository.gamesRepository.retreivePlayers(gameCode), player)
    }

    @Test
    fun `create player should fail if game code is bad`() = runTest {
        assertFailsWith<InvalidGameCode> {
            val form = PlayerForm("Jack", "666")
            clientRepository.playersRepository.create(form)
        }
    }

    @Test
    fun `read player should get an existing player`() = runTest {
        val gameCode = testGame().code
        val form = PlayerForm("Jack", gameCode)
        val playerFromServer = serverRepository.playersRepository.create(form)
        val player = clientRepository.playersRepository.retrieve(playerFromServer.id)
        assertEquals(playerFromServer, player)
    }

    @Test
    fun `read player should fail if player doesn't exist`() = runTest {
        assertFailsWith<InvalidPlayerId> {
            clientRepository.playersRepository.retrieve(666)
        }
    }

    @Test
    fun `delete player should correctly delete player`() = runTest {
        val gameCode = testGame().code
        val form = PlayerForm("Jack", gameCode)
        val playerFromServer = serverRepository.playersRepository.create(form)
        assertTrue(serverRepository.playersRepository.hasDataWithId(playerFromServer.id))
        clientRepository.playersRepository.delete(playerFromServer.id)
        assertFalse(serverRepository.playersRepository.hasDataWithId(playerFromServer.id))
    }

    @Test
    fun `delete player should fail if player doesn't exist`() = runTest {
        assertFailsWith<InvalidPlayerId> {
            clientRepository.playersRepository.delete(666)
        }
    }
}