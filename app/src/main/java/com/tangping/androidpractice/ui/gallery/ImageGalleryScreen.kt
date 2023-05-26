package com.tangping.androidpractice.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout

interface ImageGalleryScreenCallback {
    fun onBtnCloseClick()
}

@Composable
fun ImageGalleryScreen(callback: ImageGalleryScreenCallback? = null) {
    ConstraintLayout(modifier = Modifier.background(color = Color.Black)) {
        val (btnClose) = createRefs()

        IconButton(
            onClick = {
                  callback?.onBtnCloseClick()
            },
            modifier = Modifier.constrainAs(btnClose) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        ) {
            Icon(
                Icons.Sharp.Close,
                contentDescription = "Close Button",
                tint = Color.White
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewImageGalleryScreen() {
    ImageGalleryScreen()
}