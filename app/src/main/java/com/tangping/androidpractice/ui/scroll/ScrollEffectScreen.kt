package com.tangping.androidpractice.ui.scroll

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val monthList = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Dec", "Nov",
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Dec", "Nov"
)
private val monthMap = mapOf(
    "Jan" to 0, "Feb" to 1, "Mar" to 2,
    "Apr" to 3, "May" to 4, "Jun" to 5,
    "Jul" to 6, "Aug" to 7, "Sep" to 8,
    "Oct" to 9, "Dec" to 10, "Nov" to 11
)
private val dayList = listOf(
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
)

@Composable
fun ScrollEffectScreen(
    beginMonth: String,
    beginDay: Int,
    endMonth: String,
    endDay: Int
) {
    val density = LocalDensity.current
    val itemHeightDp = remember { 38 }
    val itemHeight = density.run { itemHeightDp.dp.toPx() }
    var monthOffset by remember { mutableIntStateOf(0) }
    var dayOffset by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .height(itemHeightDp.dp)
        ) {
            LazyColumn(userScrollEnabled = false) {
                item {
                    Column(modifier = Modifier
                        .background(Color.Gray)
                        .offset { IntOffset(x = 0, y = monthOffset) }
                    ) {
                        monthList.forEach {
                            Text(
                                text = "$it ",
                                fontSize = 32.sp,
                                color = Color.Cyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.height(itemHeightDp.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            LazyColumn(userScrollEnabled = false) {
                item {
                    Column(modifier = Modifier
                        .background(Color.Black)
                        .offset { IntOffset(x = 0, y = dayOffset) }
                    ) {
                        dayList.forEach {
                            Text(
                                text = "$it",
                                fontSize = 32.sp,
                                color = Color.Green,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.height(itemHeightDp.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val beginMonthIndex = monthList.lastIndexOf(beginMonth)
        val beginDayIndex = dayList.lastIndexOf(beginDay)
        if (beginMonthIndex != -1 && beginDayIndex != -1) {
            monthOffset = -(beginMonthIndex * itemHeight).toInt()
            dayOffset = -(beginDayIndex * itemHeight).toInt()
        }
        val endMonthIndex = if ((monthMap[endMonth] ?: 0) <= (monthMap[beginMonth] ?: 0)) {
            monthList.lastIndexOf(endMonth)
        } else {
            monthList.indexOf(endMonth)
        }
        val endDayIndex = if (endDay <= beginDay) {
            dayList.lastIndexOf(endDay)
        } else {
            dayList.indexOf(endDay)
        }
        if (endMonthIndex != -1 && endDayIndex != -1) {
            val endMonthOffset = -(endMonthIndex * itemHeight).toInt()
            animate(
                initialValue = monthOffset.toFloat(),
                targetValue = endMonthOffset.toFloat(),
                animationSpec = tween(durationMillis = 300, delayMillis = 1000)
            ) { value, _ ->
                monthOffset = value.toInt()
            }
            val endDayOffset = -(endDayIndex * itemHeight).toInt()
            animate(
                initialValue = dayOffset.toFloat(),
                targetValue = endDayOffset.toFloat(),
                animationSpec = tween(durationMillis = 1300, delayMillis = 1000)
            ) { value, _ ->
                dayOffset = value.toInt()
            }
        }
    }
}