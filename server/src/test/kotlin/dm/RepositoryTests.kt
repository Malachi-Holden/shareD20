package dm

import MockGenerator
import com.holden.*
import com.holden.dieRolls.DieRollsTable
import com.holden.dm.*
import com.holden.game.GameEntity
import com.holden.game.GamesTable
import com.holden.player.PlayersTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var dmsRepository: CrdRepository<Int, Pair<DMForm, String>, DM>

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
        val dm = DMEntity.new {
            name = "Jack"
            game = newGame
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