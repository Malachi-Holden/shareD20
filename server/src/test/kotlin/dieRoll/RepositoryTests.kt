package dieRoll

import com.holden.DieRollsRepository
import com.holden.InvalidDieRollId
import com.holden.InvalidGameCode
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.DieRollsPostgresRepository
import com.holden.dieRolls.toModel
import com.holden.dm.DMEntity
import com.holden.game.GameEntity
import org.koin.test.KoinTest
import org.koin.test.get
import runTransactionTest
import setupRepositoryTestSuite
import tearDownRepositoryTestSuite
import kotlin.test.*

class RepositoryTests: KoinTest {
    lateinit var dieRollsRepository: DieRollsRepository

    @BeforeTest
    fun setup() {
        setupRepositoryTestSuite { DieRollsPostgresRepository() }
        dieRollsRepository = get()
    }

    @AfterTest
    fun tearDown() {
        tearDownRepositoryTestSuite()
    }

    @Test
    fun `create dieRoll should add it to the database`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val dieRoll = dieRollsRepository.create(DieRollForm(newGame.code.value, 20, DieRollVisibility.All))
        assertEquals(newGame.dieRolls.first().toModel(), dieRoll)
        val dieRollInDatabase = DieRollEntity.findById(dieRoll.id)!!.toModel()
        assertEquals(dieRollInDatabase, dieRoll)
    }

    @Test
    fun `create dieRoll should fail if gamecode is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            dieRollsRepository.create(DieRollForm("666", 20, DieRollVisibility.All))
        }
    }

    @Test
    fun `read dieRoll should return the correct dieRoll`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val newDieRoll = DieRollEntity.new {
            value = 20
            game = newGame
            visibility = DieRollVisibility.All.ordinal
        }
        val dieRollFromRepository = dieRollsRepository.read(newDieRoll.id.value)
        assertEquals(newDieRoll.toModel(), dieRollFromRepository)
    }

    @Test
    fun `read dieRoll should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidDieRollId> {
            dieRollsRepository.read(666)
        }
    }

    @Test
    fun `delete dieRoll should remove it from database`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val newDieRoll = DieRollEntity.new {
            value = 20
            game = newGame
            visibility = DieRollVisibility.All.ordinal
        }.toModel()
        assertNotNull(DieRollEntity.findById(newDieRoll.id))
        dieRollsRepository.delete(newDieRoll.id)
        assertNull(DieRollEntity.findById(newDieRoll.id))
    }
}