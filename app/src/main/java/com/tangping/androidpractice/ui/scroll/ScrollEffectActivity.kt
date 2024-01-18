package com.tangping.androidpractice.ui.scroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ScrollEffectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScrollEffectScreen(
                beginMonth = "May",
                beginDay = 19,
                endMonth = "Apr",
                endDay = 9
            )
        }
    }
}