import com.holden.*
import com.holden.dm.DMForm
import com.holden.game.GameForm
import com.holden.player.PlayerForm
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import util.assertEventually
import util.toMultiSet
import kotlin.test.*

class D20ViewModelTests : KoinTest {
    lateinit var repository: D20Repository
    lateinit var viewModel: D20ViewModel

    @BeforeTest
    fun setup() {
        val composeTestModule = module {
            single <D20Repository> { MockD20Repository(30) }
            viewModelOf<D20ViewModel>(constructor = { D20ViewModel() })
        }
        startKoin {
            modules(composeTestModule)
        }
        repository = get()
        viewModel = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `goToCreateGame should put the view into the creategame state`() {
        viewModel.goToCreateGame()
        assertEquals(AppState.CreateGame, viewModel.getCurrentAppState())
    }

    @Test
    fun `goToJoinGame should put the view into the joinGame state`() {
        viewModel.goToJoinGame()
        assertEquals(AppState.JoinGame, viewModel.getCurrentAppState())
    }

    @Test
    fun `goToPlayingGame should put the view into the playinggame state`() = runTest {
        val game = repository.gamesRepository.create(GameForm("test game", DMForm("Test DM")))
        val player = repository.playersRepository.create(PlayerForm("James", game.code))
        viewModel.goToPlayingGame(player, game)
        assertEquals(AppState.PlayingGame(player, game), viewModel.getCurrentAppState())
    }

    @Test
    fun `goToDMingGame should put the view into the dminggame state`() = runTest {
        val game = repository.gamesRepository.create(GameForm("test game", DMForm("Test DM")))
        viewModel.goToDMingGame(game.dm, game)
        assertEquals(AppState.DMingGame(game.dm, game), viewModel.getCurrentAppState())
    }

    @Test
    fun `onCreateGame should add the game eventually`() = runTest {
        viewModel.onCreateGame(GameForm("test game", DMForm("Test DM"))).await()
        assertIs<AppState.DMingGame>(viewModel.getCurrentAppState())
        val state = viewModel.getCurrentAppState() as AppState.DMingGame
        val game = repository.gamesRepository.retrieve(state.game.code)
        assertEquals(game, state.game)
    }

    @Test
    fun `onjoin should create the player and join the game`() = runTest {
        var game = repository.gamesRepository.create(GameForm("test game", DMForm("Test DM")))
        viewModel.onJoin(PlayerForm("john", game.code)).await()
        assertIs<AppState.PlayingGame>(viewModel.getCurrentAppState())
        val state = viewModel.getCurrentAppState() as AppState.PlayingGame
        val player = repository.playersRepository.retrieve(state.player.id)
        game = repository.gamesRepository.retrieve(game.code)
        assertEquals(player, state.player)
        assertEquals(game, state.game)
    }

    @Test
    fun `onjoin should go into error state if game doesn't exist`() = runTest {
        viewModel.onJoin(PlayerForm("john", "666")).await()
        assertIs<AppState.ErrorState>(viewModel.getCurrentAppState())
        val state = viewModel.getCurrentAppState() as AppState.ErrorState
        assertIs<InvalidGameCode>(state.error)
    }

    @Test
    fun `current players should have correct players`() = runTest {
        val game = repository.gamesRepository.create(GameForm("test game", DMForm("Test DM")))
        val player = repository.playersRepository.create(PlayerForm("James", game.code))
        val dmplayer = repository.playersRepository.retrieve(game.dm.playerId)
        assertEquals("Test DM", dmplayer.name)
        viewModel.goToPlayingGame(player, game)
        assertEquals(viewModel.getCurrentAppState(), AppState.PlayingGame(player, game))
        assertEventually(1000) {
            listOf(player, dmplayer).toMultiSet() == viewModel.getCurrentPlayers().toMultiSet()
        }
    }
}