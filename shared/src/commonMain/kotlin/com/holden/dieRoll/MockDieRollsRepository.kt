package com.holden.dieRoll

import com.holden.InvalidDieRollId
import com.holden.InvalidGameCode
import com.holden.InvalidPlayerId
import com.holden.generateSequentialIds
import kotlinx.coroutines.delay

class MockDieRollsRepository(
    val delayMS: Long = 0,
    val gameExists: (code: String) -> Boolean,
    val playerExists: (id: Int) -> Boolean
): DieRollsRepository {
    private val generateDieRollIds: Iterator<Int> = generateSequentialIds().iterator()
    val dieRolls: MutableMap<Int, DieRoll> = mutableMapOf()

    override suspend fun create(form: DieRollForm): DieRoll {
        delay(delayMS)
        if (!gameExists(form.gameCode)) throw InvalidGameCode(form.gameCode)
        if (!playerExists(form.rolledBy)) throw InvalidPlayerId(form.rolledBy)
        val id = generateDieRollIds.next()
        val dieRoll = form.toDieRoll(id)
        dieRolls[id] = dieRoll
        return dieRoll
    }

    override suspend fun retrieve(id: Int): DieRoll {
        delay(delayMS)
        return dieRolls[id] ?: throw InvalidDieRollId(id)
    }

    override suspend fun delete(id: Int) {
        delay(delayMS)
        dieRolls.remove(id) ?: throw InvalidDieRollId(id)
    }
}