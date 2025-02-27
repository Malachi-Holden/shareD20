import com.holden.*
import com.holden.dieRolls.DieRollsTable
import com.holden.dms.*
import com.holden.games.*
import com.holden.players.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.*

class MockGenerator: GenerateCodes {
    val generator = generateSequentialGameCodes()
    override fun next(): String = generator.next()
}

class RepositoryTests: KoinTest {
    lateinit var repository: D20Repository
    lateinit var testDM: DMForm

    @BeforeTest
    fun setup() {
        val repositoryTestModule = org.koin.dsl.module {
            single<DatabaseFactory> { InMemoryDatabaseFactory }
            single<GenerateCodes> { MockGenerator() }
            single<D20Repository> { PostgresRepository() }
        }
        startKoin {
            modules(repositoryTestModule)
        }
        get<DatabaseFactory>().connect()
        transaction {
            SchemaUtils.create(GamesTable)
            SchemaUtils.create(PlayersTable)
            SchemaUtils.create(DMsTable)
            SchemaUtils.create(DieRollsTable)
        }
        repository = get()
        testDM = DMForm("jack")
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(PlayersTable)
            SchemaUtils.drop(DMsTable)
            SchemaUtils.drop(DieRollsTable)
            SchemaUtils.drop(GamesTable)
        }
        stopKoin()
    }

    @Test
    fun `addgame should create a game with the correct attributes`() = runTransactionTest {
        val form = GameForm("Hello world", testDM)
        val game = repository.gamesRepository.create(form)
        assertEquals(form.name, game.name)
        assertEquals(form.dm.name, game.dm.name)
        val gottenGame = GameEntity.findById(game.code)?.toModel()
        assertEquals(game, gottenGame)
    }

    @Test
    fun `getGame should get the correct game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val gameFromRepo = repository.gamesRepository.read("00000000")
        assertEquals(newGame.toModel(), gameFromRepo)
    }

    @Test
    fun `getGame should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            repository.gamesRepository.read("666")
        }
    }

    @Test
    fun `deletegame should remove the game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }

        assert(repository.gamesRepository.hasDataWithId("00000000"))
        repository.gamesRepository.delete("00000000")
        assertFalse(repository.gamesRepository.hasDataWithId("00000000"))
    }

    @Test
    fun `deletegame should fail if code is bad`() = runTransactionTest {
        assertFailsWith<InvalidGameCode> {
            repository.gamesRepository.delete("666")
        }
    }

    @Test
    fun `deletegame should delete all its players and its dm`() = runTest {
        lateinit var newGame: GameEntity
        lateinit var dm: DM
        lateinit var player1: Player
        lateinit var player2: Player
        transaction {
            newGame = GameEntity.new("00000000") {
                name = "Hello world"
            }
            dm = DMEntity.new {
                name = "Jack"
                game = newGame
            }.toModel()
            player1 = PlayerEntity.new {
                name = "john"
                game = newGame
            }.toModel()
            player2 = PlayerEntity.new {
                name = "jane"
                game = newGame
            }.toModel()
        }
        repository.gamesRepository.delete(newGame.code.value)
        transaction {
            assertNull(DMEntity.findById(dm.id))
            assertNull(PlayerEntity.findById(player1.id))
            assertNull(PlayerEntity.findById(player2.id))
        }
    }

    @Test
    fun `creating a player should correctly add the player to the specified game`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        assertEquals(0, newGame.players.count())
        val player = repository.playersRepository.create(PlayerForm("john", newGame.code.value))
        assertEquals(1, newGame.players.count())
        assertEquals(player, newGame.players.first().toModel())
    }

    @Test
    fun `getPlayer should return the correct player`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val player = PlayerEntity.new {
            name = "john"
            game = newGame
        }
        val playerFromRepo = repository.playersRepository.read(player.id.value)
        assertEquals(player.toModel(), playerFromRepo)
    }

    @Test
    fun `getplayer should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            repository.playersRepository.read(666)
        }
    }

    @Test
    fun `deletePlayer should delete correct player`() = runTransactionTest {
        val newGame = GameEntity.new("00000000") {
            name = "Hello world"
        }
        DMEntity.new {
            name = "Jack"
            game = newGame
        }
        val player = PlayerEntity.new {
            name = "john"
            game = newGame
        }
        val playerId = player.id.value
        assertEquals(newGame.players.first().toModel(), player.toModel())
        repository.playersRepository.delete(playerId)
        assertEquals(0, newGame.players.count())
        assertNull(PlayerEntity.findById(playerId))
    }

    @Test
    fun `deletePlayer should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidPlayerId> {
            repository.playersRepository.delete(666)
        }
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
        assertEquals(dm.toModel(), repository.dmsRepository.read(dm.id.value))
    }

    @Test
    fun `getDM should fail if id is bad`() = runTransactionTest {
        assertFailsWith<InvalidDMId> {
            repository.dmsRepository.read(666)
        }
    }
}