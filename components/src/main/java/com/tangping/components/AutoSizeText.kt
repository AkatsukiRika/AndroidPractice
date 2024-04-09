package com.tangping.components

import androidx.annotation.IntDef
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

const val CONSTRAINT_HEIGHT = 0
const val CONSTRAINT_WIDTH = 1

@IntDef(CONSTRAINT_HEIGHT, CONSTRAINT_WIDTH)
annotation class AutoSizeConstraint

const val REDUCE_MODE_SP = 10
const val REDUCE_MODE_PERCENT = 11

@IntDef(REDUCE_MODE_SP, REDUCE_MODE_PERCENT)
annotation class AutoSizeReduceMode

@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    style: TextStyle,
    @AutoSizeConstraint constraint: Int,
    @AutoSizeReduceMode reduceMode: Int = REDUCE_MODE_SP,
    lineHeightFactor: Int = 2,
    fitMaxWord: Boolean = false,
    stepGranularity: Float = 1f,
    minTextSize: TextUnit = 6.sp,
    onFontSizeChanged: ((TextUnit) -> Unit)? = null
) {
    var textStyle by remember(style) { mutableStateOf(style) }
    val textMeasure = rememberTextMeasurer()

    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        inlineContent = inlineContent,
        style = textStyle,
        onTextLayout = { result ->
            fun constrain(reduceMode: Int, lineHeightFactor: Int) {
                val reducedSize = if (reduceMode == REDUCE_MODE_SP) {
                    (textStyle.fontSize.value - stepGranularity).sp
                } else {
                    textStyle.fontSize * stepGranularity
                }
                if (minTextSize != TextUnit.Unspecified && reducedSize <= minTextSize) {
                    textStyle = textStyle.copy(fontSize = minTextSize, lineHeight = (minTextSize.value + lineHeightFactor).sp)
                    onFontSizeChanged?.invoke(minTextSize)
                } else {
                    textStyle = textStyle.copy(fontSize = reducedSize, lineHeight = (reducedSize.value + lineHeightFactor).sp)
                    onFontSizeChanged?.invoke(reducedSize)
                }
            }

            when (constraint) {
                CONSTRAINT_HEIGHT -> {
                    if (result.didOverflowHeight) {
                        constrain(reduceMode, lineHeightFactor)
                    } else {
                        if (fitMaxWord) {
                            // If the longest word is wrapped into a new line, shrink the text size
                            val split = text.split(" ")
                            var maxStr = ""
                            split.forEach {
                                if (it.length > maxStr.length) {
                                    maxStr = it
                                }
                            }

                            val maxTextWidth = textMeasure.measure(maxStr, textStyle).size.width
                            if (maxTextWidth > result.size.width) {
                                constrain(reduceMode, lineHeightFactor)
                            }
                        }
                    }
                }
                CONSTRAINT_WIDTH -> {
                    if (result.didOverflowWidth) {
                        constrain(reduceMode, lineHeightFactor)
                    }
                }
            }
        }
    )
}