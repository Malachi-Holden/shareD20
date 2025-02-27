package com.holden

/**
 * Generalized representation of a repository that can create, read, and delete objects
 * @param I The id type used to look up objects
 * @param F The form type used to specify data about the object to create it
 * @param D The data type stored in this repository
 */
interface CrdRepository<I, F, D> {
    suspend fun create(form: F): D
    suspend fun read(id: I): D
    suspend fun delete(id: I)
}

suspend fun <I, F, D>CrdRepository<I, F, D>.hasDataWithId(id: I): Boolean = try {
    read(id)
    true
} catch (e: ShareD20IAE) {
    false
}