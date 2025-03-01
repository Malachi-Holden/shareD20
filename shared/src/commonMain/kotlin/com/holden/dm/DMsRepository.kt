package com.holden.dm

import com.holden.CrdRepository

interface DMsRepository: CrdRepository<Int, Pair<DMForm, String>, DM>