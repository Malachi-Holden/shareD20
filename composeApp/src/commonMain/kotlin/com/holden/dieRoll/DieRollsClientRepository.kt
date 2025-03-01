package com.holden.dieRoll

import io.ktor.client.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DieRollsClientRepository: DieRollsRepository, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: DieRollForm): DieRoll {
        TODO("Not yet implemented")
    }

    override suspend fun retrieve(id: Int): DieRoll {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}