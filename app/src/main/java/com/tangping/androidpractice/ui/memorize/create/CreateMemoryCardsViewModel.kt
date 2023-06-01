package com.tangping.androidpractice.ui.memorize.create

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateMemoryCardsViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "CreateMemoryCardsViewModel"
    }

    var viewStates by mutableStateOf(CreateMemoryCardsStates())
        private set

    fun dispatch(action: CreateMemoryCardsAction, context: Context) {
        when (action) {
            CreateMemoryCardsAction.ScanCacheDirectory -> {
                viewModelScope.launch(Dispatchers.Main) {
                    val jsonFiles = scanCacheDirectory(context)
                    viewStates = viewStates.copy(
                        jsonFiles = jsonFiles
                    )
                    Log.i(TAG, "jsonFiles size=${jsonFiles.size}")
                }
            }
        }
    }

    private suspend fun scanCacheDirectory(context: Context) =
        withContext(Dispatchers.IO) {
            val cacheDir = context.cacheDir
            val jsonFiles = mutableListOf<String>()
            cacheDir?.listFiles()?.forEach { file ->
                if (file.isFile && file.name.endsWith(".json")) {
                    jsonFiles.add(file.name)
                }
            }
            jsonFiles
        }
}

data class CreateMemoryCardsStates(
    val jsonFiles: MutableList<String> = mutableListOf()
)

sealed class CreateMemoryCardsAction {
    object ScanCacheDirectory : CreateMemoryCardsAction()
}