package player

import assertContentEqualsOrderless
import com.holden.D20Repository
import com.holden.MockD20Repository
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm
import com.holden.dieRoll.DieRollVisibility
import com.holden.dm.DMForm
import com.holden.game.Game
import com.holden.game.GameForm
import com.holden.hasDataWithId
import com.holden.player.Player
import com.holden.player.PlayerForm
import d20TestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.*

class RoutesTests: KoinTest {
    lateinit var repository: D20Repository
    lateinit var testDM: DMForm

    @BeforeTest
    fun setup() {
        val routesTestModule = module {
            single<D20Repository> { MockD20Repository() }
        }
        startKoin {
            modules(routesTestModule)
        }
        repository = get()
        testDM = DMForm("jack")
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `post player should add the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm("testgame", testDM))
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", testGame.code))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val player = response.body<Player>()
        assertEquals("Jane", player.name)
        val playerFromRepo = repository.playersRepository.retrieve(player.id)
        assertEquals(player.name, playerFromRepo.name)
    }

    @Test
    fun `post player should return 404 if the game code doesn't exist`() = d20TestApplication(repository) { client ->
        val badCode = "66666666"
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerForm("Jane", badCode))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        assertEquals("InvalidGameCode", response.bodyAsText())
    }

    @Test
    fun `get player should return the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
        val player = repository.playersRepository.create(PlayerForm("new player", testGame.code))
        val response = client.get("/players/${player.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val gottenPlayer = response.body<Player>()
        assertEquals(player, gottenPlayer)
    }

    @Test
    fun `get player should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.get("/players/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidPlayerId", response.bodyAsText())
    }

    @Test
    fun `delete Player should delete the correct player`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm("testgame", testDM))
        val player1 = repository.playersRepository.create(PlayerForm("Jane", testGame.code))
        val player2 = repository.playersRepository.create(PlayerForm("Jill", testGame.code))
        assert(repository.playersRepository.hasDataWithId(player1.id))
        assert(repository.playersRepository.hasDataWithId(player2.id))
        val response = client.delete("/players/${player1.id}")
        assert(response.status.isSuccess())
        assertFalse(repository.playersRepository.hasDataWithId(player1.id))
        assert(repository.playersRepository.hasDataWithId(player2.id))
    }

    @Test
    fun `delete player should fail if id is bad`() = d20TestApplication(repository) { client ->
        val response = client.delete("/players/666")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidPlayerId", response.bodyAsText())
    }

    @Test
    fun `get dierolls should return correct die rolls`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
        val player = repository.playersRepository.create(PlayerForm("new player", testGame.code))
        val roll1 = repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.All,
            false
        ))
        val roll2 = repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.All,
            false
        ))
        val response = client.get("/players/${player.id}/dieRolls")
        val rolls: List<DieRoll> = response.body()
        assertContentEqualsOrderless(listOf(roll1, roll2), rolls)
    }

    @Test
    fun `get dieRolls should fail if player id is bad`() = d20TestApplication(repository) { client ->
        val response = client.get("/players/666/dieRolls")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("InvalidPlayerId", response.bodyAsText())
    }

    @Test
    fun `get visibleDieRolls should return correct die rolls`() = d20TestApplication(repository) { client ->
        val testGame = repository.gamesRepository.create(GameForm(name = "hello world", testDM))
        val player = repository.playersRepository.create(PlayerForm("new player", testGame.code))
        val otherPlayer = repository.playersRepository.create(PlayerForm("other player", testGame.code))
        val roll1 = repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.All,
            false
        ))
        val roll2 = repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.PrivateDM,
            false
        ))
        repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            otherPlayer.id,
            20,
            DieRollVisibility.PrivateDM,
            false
        ))
        repository.dieRollsRepository.create(DieRollForm(
            testGame.code,
            player.id,
            20,
            DieRollVisibility.BlindDM,
            false
        ))
        val response = client.get("/players/${player.id}/visibleDieRolls")
        val rolls: List<DieRoll> = response.body()
        assertContentEqualsOrderless(listOf(roll1, roll2), rolls)
    }
}