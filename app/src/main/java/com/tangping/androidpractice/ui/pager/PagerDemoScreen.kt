package com.tangping.androidpractice.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tangping.androidpractice.ui.theme.Color_F3F8FF
import com.tangping.androidpractice.widgets.SeekBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerDemoScreen() {
    var currentStep by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = { 6 },
        initialPage = 0
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color_F3F8FF)
    ) {
        Pager(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, bottom = 64.dp, start = 16.dp, end = 16.dp)
        )

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pager(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        ) {
            Text(
                text = "${it + 1}",
                color = Color.White,
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}