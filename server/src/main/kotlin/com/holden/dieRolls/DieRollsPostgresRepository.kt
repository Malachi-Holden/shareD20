package com.holden.dieRolls

import com.holden.CrdRepository
import com.holden.dieRoll.DieRoll
import com.holden.dieRoll.DieRollForm

class DieRollsPostgresRepository: CrdRepository<Int, DieRollForm, DieRoll> {
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