package com.holden.dieRoll

import com.holden.CrdRepository
import io.ktor.client.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DieRollsRepository: CrdRepository<Int, DieRollForm, DieRoll>, KoinComponent {
    val client: HttpClient by inject()

    override suspend fun create(form: DieRollForm): DieRoll {
        TODO("Not yet implemented")
    }

    override suspend fun read(id: Int): DieRoll {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}