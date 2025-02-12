import com.holden.D20Repository
import com.holden.InMemoryD20Repository
import com.holden.module
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

class D20TestRepository: D20Repository by InMemoryD20Repository(
    generateIds = generateSequentialIds()
)

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