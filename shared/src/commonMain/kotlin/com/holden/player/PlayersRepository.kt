package com.holden.player

import com.holden.CrdRepository

interface PlayersRepository: CrdRepository<Int, PlayerForm, Player>