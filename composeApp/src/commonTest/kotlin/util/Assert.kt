package util

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.test.assertEquals
import kotlin.test.fail

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

suspend fun assertEventually(timeout: Long, block: suspend () -> Boolean) {
    val startTime = Clock.System.now().toEpochMilliseconds()
    while (true) {
        if (Clock.System.now().toEpochMilliseconds() > startTime + timeout) {
            fail("timeout reached")
        }
        if (block()) {
            break
        }
        delay(10)
    }
}