package com.holden

fun interface GenerateCodes {
    fun next(): String
}

interface D20Repository {

    /**
     * Adds a new game to the repository
     * @param form Describes the game to be added
     * @return The game that was added to the repository
     */
    suspend fun addGame(form: GameForm): Game

    /**
     * Attempts to delete a game
     * @param code The code of the game to delete
     * @throws [InvalidGameCode] if [code] is null or doesn't exist in the repository
     */
    suspend fun deleteGame(code: String?)

    /**
     * Gets a game from the repository
     * @param code The code of the game to get
     * @throws [InvalidGameCode] if [code] is null or doesn't exist in the repository
     */
    suspend fun getGameByCode(code: String?): Game

    /**
     * Checks if a game exists in the repository
     * @param code The code to check
     * @return [true] if the game exists, [false] otherwise
     */
    suspend fun hasGameWithCode(code: String?): Boolean

    /**
     * Attempts to create a player
     * @param form Describes the player to be added
     * @throws [InvalidPlayerId] if the provided id doesn't exist in the repository
     * @return The player that was added to the repository
     */
    suspend fun createPlayer(form: PlayerForm): Player

    /**
     * Attempts to delete a player
     * @param id Id of the player to delete
     * @throws [InvalidPlayerId] if [id] is null or doesn't exist in the repository
     */
    suspend fun deletePlayer(id: Int?)

    /**
     * Gets a player from the repository
     * @param id Id of the player to get
     * @throws [InvalidPlayerId] if [id] is null or doesn't exist in the repository
     * @return The player
     */
    suspend fun getPlayer(id: Int?): Player

    /**
     * Checks if a player exists in the repository
     * @param id Id of the player to check
     * @return [true] if the player exists, [false] otherwise
     */
    suspend fun hasPlayer(id: Int?): Boolean

    /**
     * Gets a DM from the repository
     * @param id Id of the DM to get
     * @throws [InvalidDMId] if [id] is null or doesn't exist in the repository
     * @return The DM
     */
    suspend fun getDM(id: Int?): DM

    /**
     * Checks if a DM exists in the repository
     * @param id Id of the DM to check
     * @return [true] if the DM exists, [false] otherwise
     */
    suspend fun hasDM(id: Int?): Boolean
}