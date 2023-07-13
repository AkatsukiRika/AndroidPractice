package com.tangping.androidpractice.ui.memorize.prepare

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tangping.androidpractice.R
import com.tangping.androidpractice.model.memorize.RemoteData
import com.tangping.androidpractice.model.memorize.RemoteDataItem
import com.tangping.androidpractice.utils.JsonUtils
import com.tangping.androidpractice.utils.NetworkUtils
import com.tangping.androidpractice.utils.SharedPreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemorizePreparationViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val REMOTE_DATA = "remote_data"
    }

    var viewStates by mutableStateOf(MemorizePreparationStates())
        private set

    private val _viewEvents = Channel<MemorizePreparationEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: MemorizePreparationAction, context: Context) {
        when (action) {
            MemorizePreparationAction.ScanCacheDirectory -> {
                updateJsonFiles(context)
            }
            is MemorizePreparationAction.UseRemoteData -> {
                useRemoteData(context, action.url)
            }
        }
    }

    private fun useRemoteData(context: Context, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteCacheId = SharedPreferenceUtils.getInt(context, SharedPreferenceUtils.REMOTE_CACHE_ID)
            val fileName = "${REMOTE_DATA}_${remoteCacheId}.json"
            remoteCacheId.let {
                SharedPreferenceUtils.putInt(context, SharedPreferenceUtils.REMOTE_CACHE_ID, remoteCacheId + 1)
            }
            updateRemoteDataJson(context, fileName, url)
            try {
                val saveResult = NetworkUtils.downloadAndSaveJson(context, url, fileName)
                if (saveResult) {
                    viewModelScope.launch(Dispatchers.Main) {
                        _viewEvents.send(MemorizePreparationEvent.DismissUrlPopup)
                        _viewEvents.send(MemorizePreparationEvent.GoRecall(fileName))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast(context.getString(R.string.fetch_remote_failure))
            }
        }
    }

    private fun updateRemoteDataJson(context: Context, fileName: String, url: String) {
        val remoteDataJson = SharedPreferenceUtils.getString(context, SharedPreferenceUtils.REMOTE_DATA_JSON)
        try {
            val gson = Gson()
            var remoteData = gson.fromJson(remoteDataJson, RemoteData::class.java)
            if (remoteData == null) {
                remoteData = RemoteData()
            }
            val newItem = RemoteDataItem(fileName, url)
            remoteData.items.add(newItem)
            SharedPreferenceUtils.putString(
                context,
                SharedPreferenceUtils.REMOTE_DATA_JSON,
                gson.toJson(remoteData)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _viewEvents.send(MemorizePreparationEvent.ShowToast(message))
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

sealed class MemorizePreparationEvent {
    data class ShowToast(val message: String) : MemorizePreparationEvent()
    data class GoRecall(val fileName: String) : MemorizePreparationEvent()
    object DismissUrlPopup : MemorizePreparationEvent()
}

sealed class MemorizePreparationAction {
    object ScanCacheDirectory : MemorizePreparationAction()
    data class UseRemoteData(val url: String) : MemorizePreparationAction()
}