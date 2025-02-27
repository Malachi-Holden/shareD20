package com.holden.di

import com.holden.*
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoin() = startKoin {
    modules(serverModule)
}.koin

enum class ConnectionType {
    InMemory, PostGres;

    companion object {
        fun getFromArgs(args: Array<String>): ConnectionType {
            val useTempDatabaseArg = args
                .firstOrNull { it.startsWith("--tempDatabase=") }
                ?.removePrefix("--tempDatabase=")
            return if (useTempDatabaseArg == "true") {
                InMemory
            } else {
                PostGres
            }
        }
    }
}

val serverModule = module {
    single<DatabaseFactory>(named(ConnectionType.InMemory)) { InMemoryDatabaseFactory }
    single<DatabaseFactory>(named(ConnectionType.PostGres)) { PostGresDatabseFacotry }
    single <GenerateCodes> { StandardGenerator }
    single <D20RepositoryOld>{ PostgresD20Repository() }
}