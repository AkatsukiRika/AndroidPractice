package com.tangping.androidpractice.ui.pager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class PagerDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PagerDemoScreen()
        }
    }
}