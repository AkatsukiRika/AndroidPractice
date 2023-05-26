package com.tangping.androidpractice.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tangping.androidpractice.R

interface HomeScreenCallback {
    fun onImageGalleryClick()
}

@Composable
fun HomeScreen(callback: HomeScreenCallback? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            callback?.onImageGalleryClick()
        }) {
            Text(text = stringResource(id = R.string.image_gallery))
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewHomeScreen() {
    HomeScreen()
}