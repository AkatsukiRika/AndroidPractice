package com.tangping.androidpractice.widgets

import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tangping.androidpractice.ui.theme.Color_607274
import com.tangping.androidpractice.ui.theme.Color_711DB0
import com.tangping.androidpractice.ui.theme.Color_9BB8CD
import com.tangping.androidpractice.utils.ext.toDp
import com.tangping.androidpractice.utils.ext.toPx
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

data class SeekBarDimen(
    val dpBeforeFirstDot: Int = 12,
    val dpAfterLastDot: Int = 8,
    val dpBetweenDots: Int = 48,
    val dpSliderSize: Int = 30,
    val dpDotSize: Int = 8,
    val dpHighlightDotMargin: Int = 5
)

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    currentStep: Int,
    totalSteps: Int,
    initialStep: Int? = null,
    dimen: SeekBarDimen = SeekBarDimen(),
    onStepChange: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    // Dimen
    val dpSliderSize = dimen.dpSliderSize
    val pxSliderSize = dpSliderSize.dp.toPx()
    val dpDotSize = dimen.dpDotSize
    val pxDotSize = dpDotSize.dp.toPx()
    // State
    val pxDotCenters = remember { mutableStateMapOf<Int, Float>() }
    val pxHighlightDotCenters = remember { mutableStateMapOf<Int, Float>() }
    var pxSliderOffset by remember { mutableFloatStateOf(0f) }
    var pxRowWidth by remember { mutableIntStateOf(0) }

    Box(modifier = modifier) {
        NormalRow(
            modifier = Modifier.align(Alignment.Center),
            onRowPositioned = {
                pxRowWidth = it.size.width
            },
            onDotPositioned = { it, step ->
                pxDotCenters[step] = it.positionInParent().x + pxDotSize / 2
            },
            dimen = dimen,
            totalSteps = totalSteps,
            onStepChange = onStepChange
        )

        if (initialStep != null && initialStep != currentStep) {
            val highlightSteps = abs(initialStep - currentStep) + 1
            HighlightRow(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset {
                        val pxDotCenterCurrent = pxDotCenters[currentStep] ?: 0f
                        val pxHighlightDotCenter = if (currentStep > initialStep) {
                            pxHighlightDotCenters[highlightSteps - 1] ?: 0f
                        } else {
                            pxDotCenters[0] ?: 0f
                        }
                        IntOffset(
                            x = (pxDotCenterCurrent - pxHighlightDotCenter).roundToInt(),
                            y = 0
                        )
                    },
                dimen = dimen,
                highlightSteps = highlightSteps,
                onDotPositioned = { it, step ->
                    pxHighlightDotCenters[step] = it.positionInParent().x + pxDotSize / 2
                }
            )
        }

        Slider(
            dpSliderSize = dpSliderSize,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = pxSliderOffset.toDp())
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            val nearestDotIndex =
                                getNearestDotIndex(pxSliderOffset, pxSliderSize, pxDotCenters)
                            onStepChange(nearestDotIndex)
                        },
                        onDragCancel = {
                            val nearestDotIndex =
                                getNearestDotIndex(pxSliderOffset, pxSliderSize, pxDotCenters)
                            onStepChange(nearestDotIndex)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        pxSliderOffset = (pxSliderOffset + dragAmount.x).coerceIn(
                            minimumValue = 0f,
                            maximumValue = pxRowWidth.toFloat() - pxSliderSize
                        )
                    }
                }
        )
    }

    LaunchedEffect(key1 = currentStep) {
        val newOffset = getOffsetByDotIndex(pxDotCenters.toMap(), pxSliderSize, currentStep)
        scope.launch {
            animate(
                initialValue = pxSliderOffset,
                targetValue = newOffset
            ) { value, _ ->
                pxSliderOffset = value
            }
        }
    }
}

private fun getOffsetByDotIndex(pxDotCenters: Map<Int, Float>, pxSliderSize: Float, dotIndex: Int): Float {
    pxDotCenters[dotIndex]?.let {
        return it - pxSliderSize / 2
    }
    return 0f
}

private fun getNearestDotIndex(pxSliderOffset: Float, pxSliderSize: Float, pxDotCenters: Map<Int, Float>): Int {
    val pxSliderCenter = pxSliderOffset + pxSliderSize / 2
    var minDistance = Float.MAX_VALUE
    var minIndex = -1
    pxDotCenters.forEach {
        val dotIndex = it.key
        val pxDotCenter = it.value
        val distance = abs(pxDotCenter - pxSliderCenter)
        if (distance < minDistance) {
            minDistance = distance
            minIndex = dotIndex
        }
    }
    return minIndex
}

@Composable
private fun NormalRow(
    modifier: Modifier,
    onRowPositioned: (LayoutCoordinates) -> Unit,
    onDotPositioned: (LayoutCoordinates, Int) -> Unit,
    dimen: SeekBarDimen,
    totalSteps: Int,
    onStepChange: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .height(18.dp)
            .background(Color_9BB8CD, shape = RoundedCornerShape(9.dp))
            .onGloballyPositioned {
                onRowPositioned(it)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidthSpacer(width = dimen.dpBeforeFirstDot.dp)

        for (step in 0 until totalSteps) {
            Dot(
                dpDotSize = dimen.dpDotSize,
                modifier = Modifier
                    .onGloballyPositioned {
                        onDotPositioned(it, step)
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onStepChange(step)
                    }
            )

            if (step != totalSteps - 1) {
                WidthSpacer(width = dimen.dpBetweenDots.dp)
            }
        }

        WidthSpacer(width = dimen.dpAfterLastDot.dp)
    }
}

@Composable
private fun HighlightRow(
    modifier: Modifier = Modifier,
    dimen: SeekBarDimen,
    highlightSteps: Int,
    onDotPositioned: (LayoutCoordinates, Int) -> Unit,
) {
    Row(
        modifier = modifier
            .height(18.dp)
            .background(Color_711DB0, shape = RoundedCornerShape(9.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidthSpacer(width = dimen.dpBeforeFirstDot.dp)

        for (step in 0 until highlightSteps) {
            Dot(
                dpDotSize = dimen.dpDotSize,
                highlight = true,
                modifier = Modifier.onGloballyPositioned {
                    onDotPositioned(it, step)
                }
            )

            if (step != highlightSteps - 1) {
                WidthSpacer(width = dimen.dpBetweenDots.dp)
            }
        }

        WidthSpacer(width = dimen.dpHighlightDotMargin.dp)
    }
}

@Composable
private fun Dot(
    modifier: Modifier = Modifier,
    dpDotSize: Int,
    highlight: Boolean = false
) {
    Box(
        modifier = modifier
            .size(dpDotSize.dp)
            .background(if (highlight) Color.White else Color_607274, shape = CircleShape)
    )
}

@Composable
private fun Slider(
    modifier: Modifier = Modifier,
    dpSliderSize: Int
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(dpSliderSize.dp)
                .background(Color.White, shape = CircleShape)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp)
                .background(Color_711DB0, shape = CircleShape)
        )
    }
}