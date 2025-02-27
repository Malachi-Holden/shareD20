package com.holden.player

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.holden.players.PlayerForm

@Composable
fun JoinGame(onJoin: (PlayerForm) -> Unit) {
    val (playerName, setPlayerName) = remember { mutableStateOf("") }
    val (code, setCode) = remember { mutableStateOf("") }
    Column {
        TextField(playerName, setPlayerName, placeholder = { Text("Pick a name") })
        TextField(code, { setCode(it.uppercase()) }, placeholder = { Text("Enter game code") })
        Button(onClick = {
            onJoin(PlayerForm(playerName, code))
        }) {
            Text("Join")
        }
    }
}