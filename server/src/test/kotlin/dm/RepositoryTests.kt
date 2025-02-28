package dm

import com.holden.DMsRepository
import com.holden.InvalidDMId
import com.holden.dm.DMEntity
import com.holden.dm.DMsPostgresRepository
import com.holden.dm.toModel
import com.holden.game.GameEntity
import com.holden.player.PlayerEntity
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var dmsRepository: DMsRepository

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite { DMsPostgresRepository() }
        dmsRepository = get()
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }


    @Test
    fun `getDM should return correctDM`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        val dm = DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        assertEquals(dm.toModel(), dmsRepository.read(dm.id.value))
    }

    @Test
    fun `getDM should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidDMId> {
            dmsRepository.read(666)
        }
    }
}