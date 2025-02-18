package com.holden.util

import kotlin.random.Random

fun uniqueRandomStringIterator(
    length: Int,
    random: Random = Random,
    charList: List<Char> = ('0'..'9') + ('A'..'Z'),
    hasBeenUsed: (String) -> Boolean
): Iterator<String> {
    return generateSequence {
        var candidate: String
        do {
            candidate = buildString {
                repeat(length) {
                    append(charList[random.nextInt(charList.size)])
                }
            }
            println("candidate: $candidate")
        } while (hasBeenUsed(candidate))

        candidate
    }.iterator()
}