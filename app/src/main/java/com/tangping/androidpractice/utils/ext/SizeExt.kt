package com.tangping.androidpractice.utils.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@ReadOnlyComposable
@Composable
fun Dp.toPx(): Float {
    return LocalDensity.current.run { this@toPx.toPx() }
}

@ReadOnlyComposable
@Composable
fun Int.toDp(): Dp {
    return LocalDensity.current.run {
        this@toDp.toDp()
    }
}

@ReadOnlyComposable
@Composable
fun Float.toDp(): Dp {
    return LocalDensity.current.run {
        this@toDp.toDp()
    }
}