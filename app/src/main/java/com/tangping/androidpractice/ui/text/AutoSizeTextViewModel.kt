package com.tangping.androidpractice.ui.text

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutoSizeTextViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val DEFAULT_FIXED_HEIGHT = 100
        const val DEFAULT_LINES = 4
        const val DEFAULT_FIXED_WIDTH = 320
    }

    data class UiState(
        val fixedHeightDp: Int = DEFAULT_FIXED_HEIGHT,
        val fixedLines: Int = DEFAULT_LINES,
        val fixedWidth: Int = DEFAULT_FIXED_WIDTH
    )

    private val _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState

    private fun updateState(newState: UiState) {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.value = newState
        }
    }

    fun adjustHeight(minus: Boolean) {
        val currHeight = uiState.value?.fixedHeightDp ?: DEFAULT_FIXED_HEIGHT
        val newHeight = if (minus) currHeight - 5 else currHeight + 5
        updateState(uiState.value?.copy(fixedHeightDp = newHeight) ?: UiState(fixedHeightDp = newHeight))
    }

    fun adjustLines(minus: Boolean) {
        val currLines = uiState.value?.fixedLines ?: DEFAULT_LINES
        val newLines = if (minus) currLines - 1 else currLines + 1
        updateState(uiState.value?.copy(fixedLines = newLines) ?: UiState(fixedLines = newLines))
    }

    fun adjustWidth(minus: Boolean) {
        val currWidth = uiState.value?.fixedWidth ?: DEFAULT_FIXED_WIDTH
        val newWidth = if (minus) currWidth - 10 else currWidth + 10
        updateState(uiState.value?.copy(fixedWidth = newWidth) ?: UiState(fixedWidth = newWidth))
    }
}