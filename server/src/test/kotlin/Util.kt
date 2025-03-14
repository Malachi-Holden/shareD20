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
import kotlin.test.assertEquals


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

inline fun <reified R: CrdRepository<*,*,*>> KoinTest.setupRepositoryTestSuite(
    crossinline getRepo: () -> R
) {
    val repositoryTestModule = module {
        single<DatabaseFactory> { InMemoryDatabaseFactory }
        single<GenerateCodes> { MockGenerator() }
        single<R> { getRepo() }
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
        SchemaUtils.drop(DieRollsTable)
        SchemaUtils.drop(DMsTable)
        SchemaUtils.drop(PlayersTable)
        SchemaUtils.drop(GamesTable)
    }
    stopKoin()
}

/**
 * Tests that [iterable1] and [iterable2] have the same content, ignoring order
 */
fun <T>assertContentEqualsOrderless(iterable1: Iterable<T>, iterable2: Iterable<T>) {
    val multiSet1 = iterable1.toMultiSet()
    val multiSet2 = iterable2.toMultiSet()
    assertEquals(multiSet1, multiSet2)
}

fun <T>Iterable<T>.toMultiSet() = buildMap {
    for (item in this@toMultiSet) {
        this[item] = (this[item] ?: 0) + 1
    }
}