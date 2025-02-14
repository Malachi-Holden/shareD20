import com.holden.D20Repository
import com.holden.Game
import com.holden.GameForm
import com.holden.module
import com.holden.util.uniqueRandomStringIterator
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*

fun generateSequentialIds(): Iterator<String>{
    var current = 0
    return generateSequence {
        (current ++).toString().padStart(8, '0')
    }.iterator()
}

class D20TestRepository: D20Repository {
    private val games: MutableMap<String, Game> = mutableMapOf()
    private val generateCodes: Iterator<String> = generateSequentialIds()

    override fun addGame(form: GameForm): Game {
        val code = generateCodes.next()
        val newGame = form.toGame(code)
        games[code] = newGame
        return newGame
    }

    override fun deleteGame(code: String?): Boolean {
        return games.remove(code) != null
    }

    override fun getGameByCode(code: String?): Game? {
        return games[code]
    }

    override fun hasGameWithCode(code: String?): Boolean {
        return games.contains(code)
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