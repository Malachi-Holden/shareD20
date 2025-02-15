import com.holden.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*

fun generateSequentialIds(): Sequence<Int>{
    var current = 0
    return generateSequence {
        (current ++)
    }
}

fun generateSequentialGameCodes() = generateSequentialIds().map { it.toString().padStart(8, '0') }.iterator()

class D20TestRepository: D20Repository {
    private val games: MutableMap<String, Game> = mutableMapOf()
    private val players: MutableMap<Int, Player> = mutableMapOf()
    private val generateCodes: Iterator<String> = generateSequentialGameCodes()
    private val generatePlayerIds: Iterator<Int> = generateSequentialIds().iterator()

    override fun addGame(form: GameForm): Game {
        val code = generateCodes.next()
        val newGame = Game(code, form.name, listOf())
        games[code] = newGame
        addPlayerToGame(form.dm.id, code)
        return games[code] ?: newGame
    }

    override fun deleteGame(code: String?): Boolean {
        return games.remove(code) != null
    }

    override fun getGameByCode(code: String?): Game? {
        return games[code]
    }

    override fun addPlayerToGame(playerId: Int?, gameCode: String?): Boolean {
        if (playerId == null || gameCode == null) return false
        val game = games[gameCode] ?: return false
        val player = players[playerId] ?: return false
        games[gameCode] = game.copy(
            players = game.players + player
        )
        players[playerId] = player.copy(
            gameCode = gameCode
        )
        return true
    }

    override fun hasGameWithCode(code: String?): Boolean {
        return games.contains(code)
    }

    override fun createPlayer(form: PlayerForm): Player {
        val id = generatePlayerIds.next()
        val player = Player(id, form.name, form.isDM, form.gameCode)
        players[id] = player
        addPlayerToGame(id, form.gameCode)
        return players[id] ?: player
    }

    override fun deletePlayer(id: Int?): Boolean {
        if (id == null || id >= players.size) return false
        return players.remove(id) != null
    }

    override fun getPlayer(id: Int?): Player? {
        return players[id]
    }

    override fun hasPlayer(id: Int?): Boolean {
        return players.containsKey(id)
    }
}

fun d20TestApplication(
    repository: D20Repository,
    block: suspend ApplicationTestBuilder.(HttpClient) -> Unit)
= testApplication {
    application {
        module(repository)
    }
    block(
        createClient {
            install(ContentNegotiation) {
                json()
            }
        }
    )
}