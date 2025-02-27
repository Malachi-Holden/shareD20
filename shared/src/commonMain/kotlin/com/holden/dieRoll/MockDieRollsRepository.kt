package com.holden.dieRoll

import com.holden.CrdRepository
import com.holden.InvalidDieRollId
import com.holden.generateSequentialIds
import kotlinx.coroutines.delay

class MockDieRollsRepository(
    val delayMS: Long = 0,
    val addDieRollToGame: (dieRoll: DieRoll, gameCode: String) -> Unit
): CrdRepository<Int, DieRollForm, DieRoll> {
    private val generateDieRollIds: Iterator<Int> = generateSequentialIds().iterator()
    val dieRolls: MutableMap<Int, DieRoll> = mutableMapOf()

    override suspend fun create(form: DieRollForm): DieRoll {
        delay(delayMS)
        val id = generateDieRollIds.next()
        val dieRoll = DieRoll(id, form.value, form.gameCode)
        dieRolls[id] = dieRoll
        addDieRollToGame(dieRoll, form.gameCode)
        return dieRoll
    }

    override suspend fun read(id: Int): DieRoll {
        delay(delayMS)
        return dieRolls[id] ?: throw InvalidDieRollId(id)
    }

    override suspend fun delete(id: Int) {
        delay(delayMS)
        dieRolls.remove(id) ?: throw InvalidDieRollId(id)
    }
}