package com.tangping.androidpractice.ui.memorize.modify

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangping.androidpractice.model.memorize.QuestionCard
import com.tangping.androidpractice.utils.JsonUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
                viewStates.questionCards.takeIf { it.isNotEmpty() }?.let {
                    setIndex(0)
                }
            }
            is ModifyMemoryCardsAction.SetIndex -> {
                setIndex(action.index)
            }
        }
    }

    private fun readJson(context: Context, fileName: String) {
        val result = JsonUtils.readJson(context, fileName)
        viewStates = viewStates.copy(
            questionCards = result.toMutableList()
        )
    }

    private fun setIndex(index: Int) {
        if (index !in viewStates.questionCards.indices) {
            Log.i(TAG, "index($index) is out of bound, size is ${viewStates.questionCards.size}")
            return
        }
        viewStates = viewStates.copy(
            currentCard = viewStates.questionCards[index]
        )
        viewStates.currentCard?.let {
            viewModelScope.launch {
                _viewEvents.send(ModifyMemoryCardsEvent.SetQuestionAnswer(
                    index, it.question, it.answer
                ))
            }
        }
    }
}

data class ModifyMemoryCardsState(
    val questionCards: MutableList<QuestionCard> = mutableListOf(),
    var currentCard: QuestionCard? = null
)

sealed class ModifyMemoryCardsEvent {
    data class SetQuestionAnswer(
        val index: Int,
        val question: String,
        val answer: String
    ) : ModifyMemoryCardsEvent()
}

sealed class ModifyMemoryCardsAction {
    data class ReadJson(val fileName: String) : ModifyMemoryCardsAction()

    data class SetIndex(val index: Int) : ModifyMemoryCardsAction()
}