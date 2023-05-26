package com.tangping.androidpractice.ui.gallery

import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImageGalleryViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1
    }

    var viewState by mutableStateOf(ImageGalleryViewState())
        private set

    fun dispatch(action: ImageGalleryViewAction, context: Context) {
        when (action) {
            is ImageGalleryViewAction.ScanGallery -> {
                scanGallery(context)
            }
        }
    }

    private fun scanGallery(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageAlbums = scanGalleryInner(context)
            viewState.albumList.apply {
                clear()
                addAll(imageAlbums)
            }
        }
    }

    private fun scanGalleryInner(context: Context): List<ImageAlbum> {
        val albums = mutableListOf<ImageAlbum>()

        val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)

        cursor?.use {
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val bucketName = cursor.getString(bucketColumn)
                val data = cursor.getString(dataColumn)

                val album = albums.find { it.albumName == bucketName }
                if (album == null) {
                    albums.add(ImageAlbum(bucketName, data, mutableListOf(data)))
                } else {
                    (album.photoPaths as MutableList).add(data)
                }
            }
        }

        return albums
    }
}

data class ImageGalleryViewState(
    val albumList: MutableList<ImageAlbum> = mutableListOf(
        ImageAlbum("All Photos", "", emptyList()),
        ImageAlbum("Camera", "", emptyList()),
        ImageAlbum("Screenshots", "", emptyList()),
        ImageAlbum("weibo", "", emptyList()),
        ImageAlbum("知乎", "", emptyList())
    )
)

data class ImageAlbum(
    val albumName: String,
    val albumPath: String,
    val photoPaths: List<String>
)

sealed class ImageGalleryViewAction {
    object ScanGallery : ImageGalleryViewAction()
}