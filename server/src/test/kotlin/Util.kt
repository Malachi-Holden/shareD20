import com.holden.*
import com.holden.dieRolls.DieRollsTable
import com.holden.dm.DM
import com.holden.dm.DMForm
import com.holden.dm.DMsPostgresRepository
import com.holden.dm.DMsTable
import com.holden.game.GamesTable
import com.holden.game.generateSequentialGameCodes
import com.holden.player.PlayersTable
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get


class MockGenerator: GenerateCodes {
    val generator = generateSequentialGameCodes()
    override fun next(): String = generator.next()
}

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

fun <I, F, D> KoinTest.setupRepositoryTestSuite(
    getRepo: () -> CrdRepository<I, F, D>
) {
    val repositoryTestModule = module {
        single<DatabaseFactory> { InMemoryDatabaseFactory }
        single<GenerateCodes> { MockGenerator() }
        single<CrdRepository<I, F, D>> { getRepo() }
    }
    startKoin {
        modules(repositoryTestModule)
    }
    get<DatabaseFactory>().connect()
    transaction {
        SchemaUtils.create(GamesTable)
        SchemaUtils.create(PlayersTable)
        SchemaUtils.create(DMsTable)
        SchemaUtils.create(DieRollsTable)
    }
}

fun tearDownRepositoryTestSuite() {
    transaction {
        SchemaUtils.drop(PlayersTable)
        SchemaUtils.drop(DMsTable)
        SchemaUtils.drop(DieRollsTable)
        SchemaUtils.drop(GamesTable)
    }
    stopKoin()
}
