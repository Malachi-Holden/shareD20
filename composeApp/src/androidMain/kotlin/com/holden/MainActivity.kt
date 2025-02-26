package com.holden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.holden.di.composeCommonModule
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinContext {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    KoinApplication(application = {
        modules(composeCommonModule)
    }) {
        App()
    }
}