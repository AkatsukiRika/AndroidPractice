package com.tangping.androidpractice.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tangping.androidpractice.ui.theme.Color_5383C3
import com.tangping.androidpractice.ui.theme.Color_68BE8D
import com.tangping.androidpractice.ui.theme.Color_BA2636
import com.tangping.androidpractice.ui.theme.Color_C8C2C6
import com.tangping.androidpractice.ui.theme.Color_E7609E
import com.tangping.androidpractice.ui.theme.Color_F3F8FF
import com.tangping.androidpractice.ui.theme.Color_F8B500
import com.tangping.androidpractice.widgets.SeekBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerDemoScreen() {
    var currentStep by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = { 6 },
        initialPage = 0
    )
    val pageList: List<@Composable () -> Unit> = listOf(
        @Composable { Page(it = 0, pagerState = pagerState) },
        @Composable { Page(it = 1, pagerState = pagerState) },
        @Composable { Page(it = 2, pagerState = pagerState) },
        @Composable { Page(it = 3, pagerState = pagerState) },
        @Composable { Page(it = 4, pagerState = pagerState) },
        @Composable { Page(it = 5, pagerState = pagerState) }
    )
    val pageMap = remember {
        mutableMapOf(
            0 to pageList[0],
            1 to pageList[1],
            2 to pageList[2],
            3 to pageList[3],
            4 to pageList[4],
            5 to pageList[5]
        )
    }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color_F3F8FF)
    ) {
        Pager(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, bottom = 64.dp, start = 16.dp, end = 16.dp),
            pageMap = pageMap
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

    LaunchedEffect(key1 = currentStep) {
        pagerState.animateScrollToPage(currentStep)
    }

    LaunchedEffect(key1 = pagerState.currentPage, key2 = pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            currentStep = pagerState.currentPage
        }
    }

    LaunchedEffect(key1 = Unit) {
        pageCallback.emit(object : PageCallback {
            override fun onSkipNext() {
                scope.launch {
                    skipNext(pagerState, pageList, pageMap)
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }

            override fun onSkipPrev() {
            }
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun skipNext(
    pagerState: PagerState,
    pageList: List<@Composable () -> Unit>,
    pageMap: MutableMap<Int, @Composable () -> Unit>
) {
    if (pagerState.currentPage + 2 <= pagerState.pageCount - 1) {
        pageMap.clear()
        var counter = 0
        for (index in pageList.indices) {
            if (index == pagerState.currentPage + 1) {
                pageMap[-1] = pageList[index]
            } else {
                pageMap[counter++] = pageList[index]
            }
        }
    }
}

private fun restorePageMap(
    pageList: List<@Composable () -> Unit>,
    pageMap: MutableMap<Int, @Composable () -> Unit>
) {
    pageMap.clear()
    for (index in pageList.indices) {
        pageMap[index] = pageList[index]
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pageMap: Map<Int, @Composable () -> Unit>
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { index ->
        pageMap[index]?.let {
            it()
        }
    }
}

private interface PageCallback {
    fun onSkipNext()
    fun onSkipPrev()
}

private var pageCallback = MutableStateFlow<PageCallback?>(null)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Page(
    it: Int,
    pagerState: PagerState
) {
    val backgroundColor = when (it) {
        0 -> Color_F8B500
        1 -> Color_5383C3
        2 -> Color_68BE8D
        3 -> Color_BA2636
        4 -> Color_E7609E
        else -> Color_C8C2C6
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)
    ) {
        Text(
            text = "${it + 1}",
            color = Color.White,
            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        if (it + 2 <= pagerState.pageCount - 1) {
            Button(
                onClick = {
                    pageCallback.value?.onSkipNext()
                },
                modifier = Modifier
                    .width(128.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Skip NEXT")
            }
        }

        if (it - 2 >= 0) {
            Button(
                onClick = {
                    pageCallback.value?.onSkipPrev()
                },
                modifier = Modifier
                    .width(128.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Skip PREV")
            }
        }
    }
}