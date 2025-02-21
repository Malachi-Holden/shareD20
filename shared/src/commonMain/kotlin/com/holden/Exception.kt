package com.holden

open class ShareD20IAE(message: String): IllegalArgumentException(message)

class InvalidGameCode(code: String?): ShareD20IAE("No game found with code $code")
class InvalidPlayerId(id: Int?): ShareD20IAE("No player found with id: $id")
class InvalidDMId(id: Int?): ShareD20IAE("No DM found with id: $id")
class NoDMFoundWithGameCode(code: String?) : ShareD20IAE("No DM found with game code: $code")
class InternalServerError(message: String): Exception(message)
class GenericHttpError(message: String): Exception(message)