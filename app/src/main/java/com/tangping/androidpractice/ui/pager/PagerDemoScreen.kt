package com.tangping.androidpractice.ui.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tangping.androidpractice.ui.theme.Color_F3F8FF
import com.tangping.androidpractice.widgets.SeekBar

@Composable
fun PagerDemoScreen() {
    var currentStep by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color_F3F8FF)
    ) {
        SeekBar(
            currentStep = currentStep,
            initialStep = 0,
            totalSteps = 6,
            onStepChange = {
                currentStep = it
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}