package com.holden

interface D20Repository {

    /**
     * Adds a new game to the repository, generating an id for it and returning a new game with the new id
     */
    fun addGame(form: GameForm): Game

    /**
     * Attempts to delete the game at id
     * Returns true if the id is non-null and a game exists with that id, false else wise
     */
    fun deleteGame(code: String?): Boolean

    /**
     * Gets a game with id, returns null if no game exists with that id (including if the id is null)
     */
    fun getGameByCode(code: String?): Game?

    /**
     * Attempts to add a player to the given game.
     * If both exist and the operation is successful this returns true, otherwise false
     */
    fun addPlayerToGame(playerId: Int?, gameCode: String?): Boolean

    /**
     * Checks if a game exists in the repository with the given code
     */
    fun hasGameWithCode(code: String?): Boolean

    fun createPlayer(form: PlayerForm): Player
    fun deletePlayer(id: Int?): Boolean
    fun getPlayer(id: Int?): Player?
    fun hasPlayer(id: Int?): Boolean
}