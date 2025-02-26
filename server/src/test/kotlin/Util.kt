import com.holden.D20Repository
import com.holden.module
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

fun d20TestApplication(
    repository: D20Repository,
    block: suspend ApplicationTestBuilder.(HttpClient) -> Unit
) = testApplication {
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

fun runTransactionTest(statement: suspend Transaction.() -> Unit) = transaction {
    runTest {
        statement()
    }
}
