package com.holden.dieRolls

import com.holden.DieRollsRepository
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm

class DieRollsPostgresRepository: DieRollsRepository {
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