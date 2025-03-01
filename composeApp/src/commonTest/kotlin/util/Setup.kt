package util

import com.holden.ClientRepository
import com.holden.D20Repository
import com.holden.D20ViewModel
import com.holden.MockD20Repository
import io.ktor.client.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class RepositoryType {
    Server, Client
}

fun setupRepositoryTestSuite() {
    val composeTestModule = module {
        single <D20Repository> (named(RepositoryType.Server)){ MockD20Repository() }
        single <HttpClient> { mockHttpClient(get(named(RepositoryType.Server))) }
        single <D20Repository> (named(RepositoryType.Client)){ ClientRepository() }
        viewModelOf<D20ViewModel>(constructor = { D20ViewModel() })
    }
    startKoin {
        modules(composeTestModule)
    }
}

fun tearDownRepositoryTestSuite() {
    stopKoin()
}