package com.tangping.androidpractice.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel

interface ImageGalleryScreenCallback {
    fun onBtnCloseClick()
}

@Composable
fun ImageGalleryScreen(
    callback: ImageGalleryScreenCallback? = null,
    viewModel: ImageGalleryViewModel = hiltViewModel()
) {
    val viewState = viewModel.viewState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.dispatch(ImageGalleryViewAction.ScanGallery, context)
    }

    ConstraintLayout(modifier = Modifier.background(color = Color.Black)) {
        val (btnClose, albumList) = createRefs()

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

        AlbumList(
            modifier = Modifier.constrainAs(albumList) {
                top.linkTo(btnClose.bottom)
                start.linkTo(parent.start)
            },
            albumItems = viewState.albumList
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewImageGalleryScreen() {
    ImageGalleryScreen()
}

@Composable
fun AlbumList(modifier: Modifier, albumItems: List<ImageAlbum>) {
    LazyRow(modifier = modifier) {
        items(albumItems) { albumItem ->
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .padding(start = 8.dp)
            ) {
                Text(text = albumItem.albumName)
            }
        }
    }
}