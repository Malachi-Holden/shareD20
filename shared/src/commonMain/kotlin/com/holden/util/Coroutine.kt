package com.holden.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> Flow<Flow<T>>.flattenConcatEarly(scope: CoroutineScope, initialValue: T): StateFlow<T> {
    val result = MutableStateFlow(initialValue)
    scope.launch {
        var job: Job? = null
        collect { parent ->
            job?.cancel()
            job = scope.launch {
                parent.collect {
                    result.value = it
                }
            }
        }
    }
    return result
}