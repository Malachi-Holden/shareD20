package com.holden.dm

import com.holden.CrdRepository
import com.holden.InvalidDMId
import com.holden.generateSequentialIds
import com.holden.util.removeAll
import kotlinx.coroutines.delay

class MockDMsRepository(
    val delayMS: Long = 0
): CrdRepository<Int, Pair<DMForm, String>, DM> {

    val dms: MutableMap<Int, DM> = mutableMapOf()
    private val generateDMIds: Iterator<Int> = generateSequentialIds().iterator()

    override suspend fun create(form: Pair<DMForm, String>): DM {
        val (dmForm, code) = form
        val dm = dmForm.toDM(generateDMIds.next(), code)
        dms[dm.id] = dm
        return dm
    }

    override suspend fun read(id: Int): DM {
        delay(delayMS)
        return dms[id] ?: throw InvalidDMId(id)
    }

    override suspend fun delete(id: Int) {
        delay(delayMS)
        dms.remove(id) ?: throw InvalidDMId(id)
    }

    fun removeDMForGame(gameCode: String) {
        dms.removeAll { _, dm -> dm.gameCode == gameCode }
    }
}