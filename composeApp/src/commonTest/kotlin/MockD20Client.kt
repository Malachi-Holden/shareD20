import com.holden.*
import com.holden.games.GameForm
import com.holden.players.PlayerForm
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun mockHttpClient(serverRepository: D20Repository) = HttpClient(
    engine = MockEngine { request ->
        val pathSegments = request.url.segments
        val bodyText = (request.body as? TextContent)?.text
        when (pathSegments.first()) {
            "games" -> {
                when (request.method) {
                    HttpMethod.Post -> postGame(bodyText, serverRepository)
                    HttpMethod.Get -> getGame(pathSegments, serverRepository)
                    HttpMethod.Delete -> deleteGame(pathSegments, serverRepository)
                    else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                }
            }
            "players" -> {
                when (request.method) {
                    HttpMethod.Post -> postPlayer(bodyText, serverRepository)
                    HttpMethod.Get -> getPlayer(pathSegments, serverRepository)
                    HttpMethod.Delete -> deletePlayer(pathSegments, serverRepository)
                    else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                }
            }
            "dms" -> {
                when (request.method) {
                    HttpMethod.Get -> getDM(pathSegments, serverRepository)
                    else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                }
            }
            else -> error("unrecognized endpoint ${request.url.encodedPath}")
        }
    },
    block = {
        install(ContentNegotiation) {
            json()
        }
    }
)
fun MockRequestHandleScope.failedToParse() = respond(
    content = """{"error": "Unable to parse body"}""",
    status = HttpStatusCode.BadRequest,
    headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
)

fun MockRequestHandleScope.notFound(message: String) = respond(
    content = message,
    status = HttpStatusCode.NotFound,
    headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
)

inline fun <reified T>MockRequestHandleScope.success(body: T) = respond(
    content = Json.encodeToString(body),
    status = HttpStatusCode.OK,
    headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
)

fun MockRequestHandleScope.noContent() = respond(
    content = "",
    status = HttpStatusCode.NoContent,
    headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
)

private suspend fun MockRequestHandleScope.postGame(
    bodyText: String?,
    serverRepository: D20Repository
): HttpResponseData {
    val form: GameForm = Json.decodeFromString(bodyText ?: return failedToParse()) ?: return failedToParse()
    val game = serverRepository.gamesRepository.create(form)
    return success(game)
}

private suspend fun MockRequestHandleScope.getGame(
    pathSegments: List<String>,
    serverRepository: D20Repository
): HttpResponseData {
    val code = pathSegments.getOrNull(1) ?: return failedToParse()
    val game = try {
        serverRepository.gamesRepository.read(code)
    } catch (e: InvalidGameCode) {
        return notFound("InvalidGameCode")
    }
    return success(game)
}

private suspend fun MockRequestHandleScope.deleteGame(
    pathSegments: List<String>,
    serverRepository: D20Repository
): HttpResponseData {
    val code = pathSegments.getOrNull(1) ?: return failedToParse()
    try {
        serverRepository.gamesRepository.delete(code)
    } catch (e: InvalidGameCode) {
        return notFound("InvalidGameCode")
    }
    return noContent()
}

private suspend fun MockRequestHandleScope.postPlayer(
    bodyText: String?,
    serverRepository: D20Repository
): HttpResponseData {
    val form: PlayerForm = Json.decodeFromString(bodyText ?: return failedToParse()) ?: return failedToParse()
    val player = serverRepository.playersRepository.create(form)
    return success(player)
}

private suspend fun MockRequestHandleScope.getPlayer(
    pathSegments: List<String>,
    serverRepository: D20Repository
): HttpResponseData {
    val id = pathSegments.getOrNull(1)?.toIntOrNull() ?: return failedToParse()
    val player = try {
        serverRepository.playersRepository.read(id)
    } catch (e: InvalidPlayerId) {
        return notFound("InvalidPlayerId")
    }
    return success(player)
}

private suspend fun MockRequestHandleScope.deletePlayer(
    pathSegments: List<String>,
    serverRepository: D20Repository
): HttpResponseData {
    val id = pathSegments.getOrNull(1)?.toIntOrNull() ?: return failedToParse()
    try {
        serverRepository.playersRepository.delete(id)
    } catch (e: InvalidPlayerId) {
        return notFound("InvalidPlayerId")
    }
    return noContent()
}

private suspend fun MockRequestHandleScope.getDM(
    pathSegments: List<String>,
    serverRepository: D20Repository
): HttpResponseData {
    val id = pathSegments.getOrNull(1)?.toIntOrNull() ?: return failedToParse()
    val dm = try {
        serverRepository.dmsRepository.read(id)
    } catch (e: InvalidDMId) {
        return notFound("InvalidDMId")
    }
    return success(dm)
}