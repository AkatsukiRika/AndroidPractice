package com.tangping.androidpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.tangping.androidpractice.ui.theme.AndroidPracticeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPApp()
        }
    }
}

@Composable
fun TPApp() {
    AndroidPracticeTheme {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            TPNavHost(navController = navController)
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewTPApp() {
    TPApp()
}