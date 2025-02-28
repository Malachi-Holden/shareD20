package dieRoll

import com.holden.DieRollsRepository
import com.holden.InvalidDieRollId
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.dieRolls.DieRollEntity
import com.holden.dieRolls.DieRollsPostgresRepository
import com.holden.dieRolls.toModel
import com.holden.dm.DMEntity
import com.holden.game.GameEntity
import com.holden.player.PlayerEntity
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
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        val dm = DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        val dieRoll = dieRollsRepository.create(
            DieRollForm(
                newGame.code.value,
                dm.player.id.value,
                20,
                DieRollVisibility.All,
                true
            )
        )
        assertEquals(newGame.dieRolls.first().toModel(), dieRoll)
        val dieRollInDatabase = DieRollEntity.findById(dieRoll.id)!!.toModel()
        assertEquals(dieRollInDatabase, dieRoll)
    }

    @Test
    fun `create dieRoll should fail if gamecode is bad`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        assertFailsWith<InvalidGameCode> {
            dieRollsRepository.create(DieRollForm("666", dmPlayer.id.value, 20, DieRollVisibility.All, true))
        }
    }

    @Test
    fun `create dieRoll should fail if player id is bad`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        assertFailsWith<InvalidPlayerId> {
            dieRollsRepository.create(DieRollForm(newGame.code.value, 666, 20, DieRollVisibility.All, true))
        }
    }

    @Test
    fun `read dieRoll should return the correct dieRoll`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        val newDieRoll = DieRollEntity.new {
            value = 20
            rolledBy = dmPlayer
            game = newGame
            visibility = DieRollVisibility.All.ordinal
            fromDM = true
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
        val dmPlayer = PlayerEntity.new {
            name = "Jack"
            game = newGame
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
            player = dmPlayer
        }
        val newDieRoll = DieRollEntity.new {
            value = 20
            rolledBy = dmPlayer
            game = newGame
            visibility = DieRollVisibility.All.ordinal
            fromDM = true
        }.toModel()
        assertNotNull(DieRollEntity.findById(newDieRoll.id))
        dieRollsRepository.delete(newDieRoll.id)
        assertNull(DieRollEntity.findById(newDieRoll.id))
    }
}