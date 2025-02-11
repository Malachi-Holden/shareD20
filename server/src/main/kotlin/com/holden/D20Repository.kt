package com.holden

interface D20Repository {

    /**
     * Adds a new game to the repository, generating an id for it and returning a new game with the new id
     */
    fun addGame(game: Game): Game

    /**
     * Attempts to delete the game at id
     * Returns true if the id is non-null and a game exists with that id, false else wise
     */
    fun deleteGame(id: String?): Boolean

    /**
     * Gets a game with id, returns null if no game exists with that id (including if the id is null)
     */
    fun getGame(id: String?): Game?

    fun hasGame(id: String?): Boolean
}