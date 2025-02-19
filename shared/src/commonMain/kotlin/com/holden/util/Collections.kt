package com.holden.util

fun <K, V>MutableMap<K, V>.removeAll(shouldRemove: (K, V) -> Boolean) = buildList {
    this@removeAll.forEach { (k, v) ->
        if (shouldRemove(k, v)) {
            add(k)
        }
    }
}.forEach {
    remove(it)
}