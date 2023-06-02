package com.tangping.androidpractice.ui.memorize.create

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangping.androidpractice.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateMemoryCardsViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "CreateMemoryCardsViewModel"
        const val JSON_SUFFIX = ".json"
    }

    var viewStates by mutableStateOf(CreateMemoryCardsStates())
        private set

    private val _viewEvents = Channel<CreateMemoryCardsEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

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
            is CreateMemoryCardsAction.CreateNewFile -> {
                createNewFile(context, action.fileName)
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

    private fun showToast(message: String) {
        viewModelScope.launch {
            _viewEvents.send(CreateMemoryCardsEvent.ShowToast(message))
        }
    }

    private fun createNewFile(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (fileName.endsWith(JSON_SUFFIX).not()) {
                showToast(context.getString(R.string.should_end_with_json))
                return@launch
            }
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            if (file.exists()) {
                file.writeText("")
            } else {
                file.createNewFile()
            }

            withContext(Dispatchers.Main) {
                _viewEvents.send(CreateMemoryCardsEvent.DismissNewFilePopup)
            }
        }
    }
}

data class CreateMemoryCardsStates(
    val jsonFiles: MutableList<String> = mutableListOf()
)

sealed class CreateMemoryCardsEvent {
    data class ShowToast(val message: String) : CreateMemoryCardsEvent()
    object DismissNewFilePopup : CreateMemoryCardsEvent()
}

sealed class CreateMemoryCardsAction {
    object ScanCacheDirectory : CreateMemoryCardsAction()
    data class CreateNewFile(val fileName: String) : CreateMemoryCardsAction()
}