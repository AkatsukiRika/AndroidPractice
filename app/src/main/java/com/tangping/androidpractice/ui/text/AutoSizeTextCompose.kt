package com.tangping.androidpractice.ui.text

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tangping.androidpractice.R
import com.tangping.components.AutoSizeText
import com.tangping.components.CONSTRAINT_HEIGHT
import com.tangping.components.CONSTRAINT_WIDTH

private const val LOREM_IPSUM_0 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore."
private const val LOREM_IPSUM_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
private const val LOREM_IPSUM_2 = "Excepteur sint occaecat cupidatat non proident"

@Composable
fun AutoSizeTextCompose() {
    var fixedHeightDp by remember { mutableIntStateOf(100) }
    var lines by remember { mutableIntStateOf(4) }
    var fixedWidthDp by remember { mutableIntStateOf(320) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.by_using_compose),
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.limit_height),
            style = TextStyle(fontSize = 24.sp, color = Color.Black),
            modifier = Modifier.padding(8.dp)
        )

        AutoSizeText(
            text = buildAnnotatedString { append(LOREM_IPSUM_0) },
            constraint = CONSTRAINT_HEIGHT,
            minTextSize = 2.sp,
            style = TextStyle(fontSize = 32.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(fixedHeightDp.dp)
                .border(width = 1.dp, color = Color.Blue)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                if (fixedHeightDp > 0) {
                    fixedHeightDp -= 5
                }
            }) {
                Text(text = "⬇", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }

            Text(
                text = "$fixedHeightDp dp",
                style = TextStyle(fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily.Monospace),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(onClick = { fixedHeightDp += 5 }) {
                Text(text = "⬆", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.limit_lines),
            style = TextStyle(fontSize = 24.sp, color = Color.Black),
            modifier = Modifier.padding(8.dp)
        )

        AutoSizeText(
            text = buildAnnotatedString { append(LOREM_IPSUM_1) },
            constraint = CONSTRAINT_HEIGHT,
            minTextSize = 2.sp,
            style = TextStyle(fontSize = 32.sp),
            maxLines = lines,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(width = 1.dp, color = Color.Blue)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                if (lines > 0) {
                    lines--
                }
            }) {
                Text(text = "⬇", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }

            Text(
                text = "$lines",
                style = TextStyle(fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily.Monospace),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(onClick = { lines++ }) {
                Text(text = "⬆", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.limit_width),
            style = TextStyle(fontSize = 24.sp, color = Color.Black),
            modifier = Modifier.padding(8.dp)
        )

        AutoSizeText(
            text = buildAnnotatedString { append(LOREM_IPSUM_2) },
            constraint = CONSTRAINT_WIDTH,
            minTextSize = 2.sp,
            style = TextStyle(fontSize = 32.sp),
            softWrap = false,
            maxLines = 1,
            modifier = Modifier
                .width(fixedWidthDp.dp)
                .height(24.dp)
                .border(width = 1.dp, color = Color.Blue)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                if (fixedWidthDp > 0) {
                    fixedWidthDp -= 10
                }
            }) {
                Text(text = "⬇", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }

            Text(
                text = "$fixedWidthDp dp",
                style = TextStyle(fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily.Monospace),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(onClick = { fixedWidthDp += 10 }) {
                Text(text = "⬆", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}