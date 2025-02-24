import com.holden.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.fail

class MockD20Client(
    val serverRepository: D20Repository = MockD20Repository()
) {
    val httpClient = HttpClient(
        engine = MockEngine { request ->
            val pathSegments = request.url.segments
            val bodyText = (request.body as? TextContent)?.text
            when (pathSegments.first()) {
                "games" -> {
                    when (request.method) {
                        HttpMethod.Post -> postGame(bodyText)
                        HttpMethod.Get -> getGame(pathSegments)
                        HttpMethod.Delete -> deleteGame(pathSegments)
                        else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                    }
                }
                "players" -> {
                    when (request.method) {
                        HttpMethod.Post -> postPlayer(bodyText)
                        HttpMethod.Get -> getPlayer(pathSegments)
                        HttpMethod.Delete -> deletePlayer(pathSegments)
                        else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                    }
                }
                "dms" -> {
                    when (request.method) {
                        HttpMethod.Get -> getDM(pathSegments)
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

    private suspend fun MockRequestHandleScope.postGame(bodyText: String?): HttpResponseData {
        val form: GameForm = Json.decodeFromString(bodyText ?: return failedToParse()) ?: return failedToParse()
        val game = serverRepository.addGame(form)
        return success(game)
    }

    private suspend fun MockRequestHandleScope.getGame(pathSegments: List<String>): HttpResponseData {
        val code = pathSegments.getOrNull(1) ?: return failedToParse()
        val game = try {
            serverRepository.getGameByCode(code)
        } catch (e: InvalidGameCode) {
            return notFound("InvalidGameCode")
        }
        return success(game)
    }

    private suspend fun MockRequestHandleScope.deleteGame(pathSegments: List<String>): HttpResponseData {
        val code = pathSegments.getOrNull(1) ?: return failedToParse()
        try {
            serverRepository.deleteGame(code)
        } catch (e: InvalidGameCode) {
            return notFound("InvalidGameCode")
        }
        return noContent()
    }

    private suspend fun MockRequestHandleScope.postPlayer(bodyText: String?): HttpResponseData {
        val form: PlayerForm = Json.decodeFromString(bodyText ?: return failedToParse()) ?: return failedToParse()
        val player = serverRepository.createPlayer(form)
        return success(player)
    }

    private suspend fun MockRequestHandleScope.getPlayer(pathSegments: List<String>): HttpResponseData {
        val id = pathSegments.getOrNull(1)?.toIntOrNull() ?: return failedToParse()
        val player = try {
            serverRepository.getPlayer(id)
        } catch (e: InvalidPlayerId) {
            return notFound("InvalidPlayerId")
        }
        return success(player)
    }

    private suspend fun MockRequestHandleScope.deletePlayer(pathSegments: List<String>): HttpResponseData {
        val id = pathSegments.getOrNull(1)?.toIntOrNull() ?: return failedToParse()
        try {
            serverRepository.deletePlayer(id)
        } catch (e: InvalidPlayerId) {
            return notFound("InvalidPlayerId")
        }
        return noContent()
    }

    private suspend fun MockRequestHandleScope.getDM(pathSegments: List<String>): HttpResponseData {
        val id = pathSegments.getOrNull(1)?.toIntOrNull() ?: return failedToParse()
        val dm = try {
            serverRepository.getDM(id)
        } catch (e: InvalidDMId) {
            return notFound("InvalidDMId")
        }
        return success(dm)
    }
}