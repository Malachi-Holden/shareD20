package dieRoll

import com.holden.*
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.dm.DMForm
import com.holden.game.GameForm
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
    suspend fun testPlayer(gameCode: String) = serverRepository.playersRepository.create(PlayerForm("Jack", gameCode))

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
    fun `create dieRoll should correctly add a dieRoll to the database`() = runTest {
        val gameCode = testGame().code
        val player = testPlayer(gameCode)
        val form = DieRollForm(
            gameCode,
            player.id,
            20,
            DieRollVisibility.All,
            false
        )
        val dieRoll = clientRepository.dieRollsRepository.create(form)
        val dieRollFromServer = serverRepository.dieRollsRepository.retrieve(dieRoll.id)
        assertEquals(dieRollFromServer, dieRoll)
        assertContains(serverRepository.playersRepository.retreiveDieRolls(player.id), dieRoll)
    }

    @Test
    fun `create dieroll should fail if game code is bad`() = runTest {
        val gameCode = testGame().code
        val player = testPlayer(gameCode)
        val form = DieRollForm(
            "666",
            player.id,
            20,
            DieRollVisibility.All,
            false
        )
        assertFailsWith<InvalidGameCode> {
            clientRepository.dieRollsRepository.create(form)
        }
    }

    @Test
    fun `create dieroll should fail if player id is bad`() = runTest {
        val gameCode = testGame().code
        val form = DieRollForm(
            gameCode,
            666,
            20,
            DieRollVisibility.All,
            false
        )
        assertFailsWith<InvalidPlayerId> {
            clientRepository.dieRollsRepository.create(form)
        }
    }

    @Test
    fun `read dieroll should get an existing dierol`() = runTest {
        val gameCode = testGame().code
        val player = testPlayer(gameCode)
        val form = DieRollForm(
            gameCode,
            player.id,
            20,
            DieRollVisibility.All,
            false
        )
        val dieRollFromServer = serverRepository.dieRollsRepository.create(form)
        val dieRoll = clientRepository.dieRollsRepository.retrieve(dieRollFromServer.id)
        assertEquals(dieRollFromServer, dieRoll)
    }

    @Test
    fun `read dieroll should fail if dieroll doesn't exist`() = runTest {
        assertFailsWith<InvalidPlayerId> {
            clientRepository.dieRollsRepository.retrieve(666)
        }
    }

    @Test
    fun `delete dieRoll should correctly delete dieroll`() = runTest {
        val gameCode = testGame().code
        val player = testPlayer(gameCode)
        val form = DieRollForm(
            gameCode,
            player.id,
            20,
            DieRollVisibility.All,
            false
        )
        val dieRollFromServer = serverRepository.dieRollsRepository.create(form)
        assertTrue(serverRepository.dieRollsRepository.hasDataWithId(dieRollFromServer.id))
        clientRepository.dieRollsRepository.delete(dieRollFromServer.id)
        assertFalse(serverRepository.dieRollsRepository.hasDataWithId(dieRollFromServer.id))
    }

    @Test
    fun `delete dieRoll should fail if dieRoll doesn't exist`() = runTest {
        assertFailsWith<InvalidDieRollId> {
            clientRepository.dieRollsRepository.delete(666)
        }
    }
}