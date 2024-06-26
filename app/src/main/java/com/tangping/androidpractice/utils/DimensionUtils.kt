package com.tangping.androidpractice.utils

import android.content.Context

object DimensionUtils {
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5).toInt()
    }
}