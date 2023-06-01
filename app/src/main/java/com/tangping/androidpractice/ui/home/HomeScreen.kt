package com.tangping.androidpractice.ui.home

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R

interface HomeScreenCallback {
    fun onImageGalleryClick()

    fun onMemoryRecallClick()

    fun onCreateMemoryCardsClick()
}

@Composable
fun HomeScreen(
    callback: HomeScreenCallback? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (activity != null && viewModel.requestGalleryPermission(activity)) {
                callback?.onImageGalleryClick()
            }
        }) {
            Text(text = stringResource(id = R.string.image_gallery))
        }

        Button(onClick = {
            callback?.onMemoryRecallClick()
        }) {
            Text(text = stringResource(id = R.string.memory_recall))
        }

        Button(onClick = {
            callback?.onCreateMemoryCardsClick()
        }) {
            Text(text = stringResource(id = R.string.create_memory_cards))
        }
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewHomeScreen() {
    HomeScreen()
}