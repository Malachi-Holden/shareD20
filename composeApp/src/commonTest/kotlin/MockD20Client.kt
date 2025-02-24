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
            val bodyText = (request.body as? TextContent)?.text
            val params = request.url.parameters
            when (request.url.encodedPath) {
                "/games" -> {
                    when (request.method) {
                        HttpMethod.Post -> postGame(bodyText)
                        HttpMethod.Get -> getGame(params)
                        HttpMethod.Delete -> deleteGame(params)
                        else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                    }
                }
                "/players" -> {
                    when (request.method) {
                        HttpMethod.Post -> postPlayer(bodyText)
                        HttpMethod.Get -> getPlayer(params)
                        HttpMethod.Delete -> deletePlayer(params)
                        else -> error("Unrecognized method ${request.method} for endpoint ${request.url.encodedPath}")
                    }
                }
                "/dms" -> {
                    when (request.method) {
                        HttpMethod.Get -> getDM(params)
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
        content = """{"error": "$message"""",
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

    private suspend fun MockRequestHandleScope.getGame(parameters: Parameters): HttpResponseData {
        val code = parameters["code"] ?: return failedToParse()
        val game = try {
            serverRepository.getGameByCode(code)
        } catch (e: InvalidGameCode) {
            return notFound(e.message ?: "")
        }
        return success(game)
    }

    private suspend fun MockRequestHandleScope.deleteGame(parameters: Parameters): HttpResponseData {
        val code = parameters["code"] ?: return failedToParse()
        try {
            serverRepository.deleteGame(code)
        } catch (e: InvalidGameCode) {
            return notFound(e.message ?: "")
        }
        return noContent()
    }

    private suspend fun MockRequestHandleScope.postPlayer(bodyText: String?): HttpResponseData {
        val form: PlayerForm = Json.decodeFromString(bodyText ?: return failedToParse()) ?: return failedToParse()
        val player = serverRepository.createPlayer(form)
        return success(player)
    }

    private suspend fun MockRequestHandleScope.getPlayer(parameters: Parameters): HttpResponseData {
        val id = parameters["id"]?.toIntOrNull() ?: return failedToParse()
        val player = try {
            serverRepository.getPlayer(id)
        } catch (e: InvalidPlayerId) {
            return notFound(e.message ?: "")
        }
        return success(player)
    }

    private suspend fun MockRequestHandleScope.deletePlayer(parameters: Parameters): HttpResponseData {
        val id = parameters["id"]?.toIntOrNull() ?: return failedToParse()
        try {
            serverRepository.deletePlayer(id)
        } catch (e: InvalidPlayerId) {
            return notFound(e.message ?: "")
        }
        return noContent()
    }

    private suspend fun MockRequestHandleScope.getDM(parameters: Parameters): HttpResponseData {
        val id = parameters["id"]?.toIntOrNull() ?: return failedToParse()
        val dm = try {
            serverRepository.getDM(id)
        } catch (e: InvalidDMId) {
            return notFound(e.message ?: "")
        }
        return success(dm)
    }
}