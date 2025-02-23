import io.ktor.client.engine.mock.*
import io.ktor.http.*

val MockClientEngine = MockEngine { request ->
    when (request.url.toString()) {
        "https://api.example.com/data" -> {
            respond(
                content = """{"message":"Hello from mock"}""",
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }
        else -> error("Unhandled request: ${request.url}")
    }
}