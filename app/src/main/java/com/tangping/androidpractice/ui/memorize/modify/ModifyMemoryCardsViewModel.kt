package com.tangping.androidpractice.ui.memorize.modify

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tangping.androidpractice.R
import com.tangping.androidpractice.model.memorize.QuestionCard
import com.tangping.androidpractice.model.memorize.RemoteData
import com.tangping.androidpractice.utils.JsonUtils
import com.tangping.androidpractice.utils.SharedPreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class ModifyMemoryCardsViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val TAG = "ModifyMemoryCardsViewModel"
    }

    var viewStates by mutableStateOf(ModifyMemoryCardsState())
        private set

    private val _viewEvents = Channel<ModifyMemoryCardsEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: ModifyMemoryCardsAction, context: Context) {
        when (action) {
            is ModifyMemoryCardsAction.ReadJson -> {
                readJson(context, action.fileName)
                if (viewStates.questionCards.isNotEmpty()) {
                    setIndex(0)
                } else {
                    addNewEntry(isEmptyList = true)
                }
            }
            is ModifyMemoryCardsAction.SetIndex -> {
                setIndex(action.index)
            }
            is ModifyMemoryCardsAction.ChangeQuestion -> {
                changeQuestion(action.question)
            }
            is ModifyMemoryCardsAction.ChangeAnswer -> {
                changeAnswer(action.answer)
            }
            is ModifyMemoryCardsAction.AddNewEntry -> {
                addNewEntry(action.index)
            }
            is ModifyMemoryCardsAction.DeleteEntry -> {
                deleteEntry(context, action.index)
            }
            is ModifyMemoryCardsAction.SaveJson -> {
                writeJson(context, action.fileName)
            }
            is ModifyMemoryCardsAction.GetRemoteData -> {
                getRemoteData(context)
            }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _viewEvents.send(ModifyMemoryCardsEvent.ShowToast(message))
        }
    }

    private fun readJson(context: Context, fileName: String) {
        val result = JsonUtils.readJson(context, fileName)
        viewStates = viewStates.copy(
            questionCards = result.toMutableList()
        )
    }

    private fun writeJson(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            JsonUtils.writeJson(context, fileName, viewStates.questionCards)
            withContext(Dispatchers.Main) {
                _viewEvents.send(ModifyMemoryCardsEvent.DismissSavePopup)
            }
        }
    }

    private fun getRemoteData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteDataJson = SharedPreferenceUtils.getString(context, SharedPreferenceUtils.REMOTE_DATA_JSON)
            try {
                val gson = Gson()
                gson.fromJson(remoteDataJson, RemoteData::class.java)?.let {
                    viewStates = viewStates.copy(remoteData = it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setIndex(index: Int) {
        if (index !in viewStates.questionCards.indices) {
            Log.i(TAG, "index($index) is out of bound, size is ${viewStates.questionCards.size}")
            return
        }
        viewStates = viewStates.copy(
            currentCard = viewStates.questionCards[index],
            currentIndex = index
        )
    }

    private fun changeQuestion(question: String) {
        val currentCard = viewStates.currentCard?.copy(question = question)
        currentCard?.let {
            viewStates.questionCards[viewStates.currentIndex] = it
        }
        viewStates = viewStates.copy(
            currentCard = currentCard
        )
    }

    private fun changeAnswer(answer: String) {
        val currentCard = viewStates.currentCard?.copy(answer = answer)
        currentCard?.let {
            viewStates.questionCards[viewStates.currentIndex] = it
        }
        viewStates = viewStates.copy(
            currentCard = currentCard
        )
    }

    private fun addNewEntry(index: Int? = null, isEmptyList: Boolean = false) {
        val newQuestionCard = QuestionCard(
            question = "",
            answer = ""
        )
        if (index == null) {
            viewStates.questionCards.add(newQuestionCard)
        } else {
            viewStates.questionCards.add(index, newQuestionCard)
        }
        viewStates = viewStates.copy(
            currentCard = newQuestionCard,
            currentIndex = if (index == null && !isEmptyList) {
                viewStates.currentIndex + 1
            } else viewStates.currentIndex
        )
    }

    private fun deleteEntry(context: Context, index: Int) {
        if (index !in viewStates.questionCards.indices) {
            Log.i(TAG, "index($index) is out of bound, size is ${viewStates.questionCards.size}")
            return
        }
        if (viewStates.questionCards.size <= 1) {
            showToast(context.getString(R.string.cannot_delete_only))
            return
        }
        val nextIndex = if (index == viewStates.questionCards.size - 1) index - 1 else index
        viewStates.questionCards.removeAt(index)
        viewStates = viewStates.copy(
            currentCard = viewStates.questionCards[nextIndex],
            currentIndex = nextIndex
        )
        viewModelScope.launch {
            _viewEvents.send(ModifyMemoryCardsEvent.DismissDeletePopup)
        }
    }

    private fun printQuestionCards() {
        viewStates.questionCards.forEach {
            Log.i(TAG, "questionCard = $it")
        }
    }
}

data class ModifyMemoryCardsState(
    val questionCards: MutableList<QuestionCard> = mutableListOf(),
    var currentCard: QuestionCard? = null,
    var currentIndex: Int = 0,
    var remoteData: RemoteData? = null
)

sealed class ModifyMemoryCardsEvent {
    data class ShowToast(val message: String) : ModifyMemoryCardsEvent()

    object DismissDeletePopup : ModifyMemoryCardsEvent()

    object DismissSavePopup : ModifyMemoryCardsEvent()
}

sealed class ModifyMemoryCardsAction {
    data class ReadJson(val fileName: String) : ModifyMemoryCardsAction()

    data class SetIndex(val index: Int) : ModifyMemoryCardsAction()

    data class ChangeQuestion(val question: String) : ModifyMemoryCardsAction()

    data class ChangeAnswer(val answer: String) : ModifyMemoryCardsAction()

    data class AddNewEntry(val index: Int? = null) : ModifyMemoryCardsAction()

    data class DeleteEntry(val index: Int) : ModifyMemoryCardsAction()

    data class SaveJson(val fileName: String) : ModifyMemoryCardsAction()

    object GetRemoteData : ModifyMemoryCardsAction()
}