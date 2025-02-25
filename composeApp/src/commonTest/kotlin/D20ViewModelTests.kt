import com.holden.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import kotlin.test.*

class D20ViewModelTests {
    lateinit var repository: D20Repository
    lateinit var viewModel: D20ViewModel
    val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        repository = MockD20Repository(30)
        viewModel = D20ViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
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
        val game = repository.addGame(GameForm("test game", DMForm("Test DM")))
        val player = repository.createPlayer(PlayerForm("James", game.code))
        viewModel.goToPlayingGame(player, game)
        assertEquals(AppState.PlayingGame(player, game), viewModel.getCurrentAppState())
    }

    @Test
    fun `goToDMingGame should put the view into the dminggame state`() = runTest {
        val game = repository.addGame(GameForm("test game", DMForm("Test DM")))
        viewModel.goToDMingGame(game.dm, game)
        assertEquals(AppState.DMingGame(game.dm, game), viewModel.getCurrentAppState())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onCreateGame should add the game eventually`() = runTest {
        viewModel.onCreateGame(GameForm("test game", DMForm("Test DM")))
        advanceUntilIdle()
        assertIs<AppState.DMingGame>(viewModel.getCurrentAppState())
        val state = viewModel.getCurrentAppState() as AppState.DMingGame
        val game = repository.getGameByCode(state.game.code)
        assertEquals(game, state.game)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onjoin should create the player and join the game`() = runTest {
        var game = repository.addGame(GameForm("test game", DMForm("Test DM")))
        viewModel.onJoin(PlayerForm("john", game.code))
        advanceUntilIdle()
        assertIs<AppState.PlayingGame>(viewModel.getCurrentAppState())
        val state = viewModel.getCurrentAppState() as AppState.PlayingGame
        val player = repository.getPlayer(state.player.id)
        game = repository.getGameByCode(game.code)
        assertEquals(player, state.player)
        assertEquals(game, state.game)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onjoin should go into error state if game doesn't exist`() = runTest {
        viewModel.onJoin(PlayerForm("john", "666"))
        advanceUntilIdle()
        assertIs<AppState.ErrorState>(viewModel.getCurrentAppState())
        val state = viewModel.getCurrentAppState() as AppState.ErrorState
        assertIs<InvalidGameCode>(state.error)
    }
}