package com.holden.dm

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.holden.dms.DMForm
import com.holden.games.GameForm

@Composable
fun CreateGame(onCreateGame: (GameForm) -> Unit) {
    val (gameTitle, setGameTitle) = remember { mutableStateOf("") }
    val (userName, setUserName) = remember { mutableStateOf("") }
    Column {
        TextField(userName, setUserName, placeholder = { Text("Choose a name for yourself") })
        TextField(gameTitle, setGameTitle, placeholder = { Text("Game title") })
        Button(onClick = {
            onCreateGame(
                GameForm(
                gameTitle,
                DMForm(userName)
            )
            )
        }) {
            Text("Create")
        }
    }
}