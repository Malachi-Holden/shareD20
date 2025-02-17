package com.holden

class InvalidGameCode(code: String?): IllegalArgumentException("No game found with code $code")
class InvalidPlayerId(id: Int?): IllegalArgumentException("No player found with id: $id")

interface D20Repository {

    /**
     * Adds a new game to the repository
     * @param form Describes the game to be added
     * @return The game that was added to the repository
     */
    fun addGame(form: GameForm): Game

    /**
     * Attempts to delete a game
     * @param code The code of the game to delete
     * @throws [InvalidGameCode] if [code] is null or doesn't exist in the repository
     */
    fun deleteGame(code: String?)

    /**
     * Gets a game from the repository
     * @param code The code of the game to get
     * @throws [InvalidGameCode] if [code] is null or doesn't exist in the repository
     */
    fun getGameByCode(code: String?): Game

    /**
     * Attempts to add a player to a game.
     * @param playerId The id of the player to add
     * @param gameCode The code of the game to add to
     * @throws [InvalidPlayerId] if [playerId] is null or doesn't exist in the repository
     * @throws [InvalidGameCode] if [gameCode] is null or doesn't exist in the repository
     */
    fun addPlayerToGame(playerId: Int?, gameCode: String?)

    /**
     * Checks if a game exists in the repository
     * @param code The code to check
     * @return [true] if the game exists, [false] otherwise
     */
    fun hasGameWithCode(code: String?): Boolean

    /**
     * Attempts to create a player
     * @param form Describes the player to be added
     * @throws [InvalidGameCode] if the provided gameCode doesn't exist in the repository
     * @return The player that was added to the repository
     */
    fun createPlayer(form: PlayerForm): Player

    /**
     * Attempts to delete a player
     * @param id Id of the player to delete
     * @throws [InvalidPlayerId] if [playerId] is null or doesn't exist in the repository
     */
    fun deletePlayer(id: Int?)

    /**
     * Gets a player from the repository
     * @param id Id of the player to get
     * @throws [InvalidPlayerId] if [playerId] is null or doesn't exist in the repository
     * @return The player
     */
    fun getPlayer(id: Int?): Player

    /**
     * Checks if a player exists in the repository
     * @param id Id of the player to check
     * @return [true] if the player exists, [false] otherwise
     */
    fun hasPlayer(id: Int?): Boolean
}