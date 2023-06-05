package com.tangping.androidpractice.ui.memorize.prepare

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangping.androidpractice.utils.JsonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemorizePreparationViewModel @Inject constructor() : ViewModel() {
    var viewStates by mutableStateOf(MemorizePreparationStates())
        private set

    fun dispatch(action: MemorizePreparationAction, context: Context) {
        when (action) {
            MemorizePreparationAction.ScanCacheDirectory -> {
                updateJsonFiles(context)
            }
        }
    }

    private fun updateJsonFiles(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            val jsonFiles = JsonUtils.scanCacheDirectory(context)
            viewStates = viewStates.copy(
                jsonFiles = jsonFiles
            )
        }
    }
}

data class MemorizePreparationStates(
    val jsonFiles: MutableList<String> = mutableListOf()
)

sealed class MemorizePreparationAction {
    object ScanCacheDirectory : MemorizePreparationAction()
}