package com.tangping.androidpractice.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BasicTag(
    modifier: Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.White)
            .background(Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewBasicTag() {
    BasicTag(modifier = Modifier, text = "REMOTE")
}