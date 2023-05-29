package com.tangping.androidpractice.ui.memorize

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangping.androidpractice.R
import com.tangping.androidpractice.model.memorize.QuestionCard
import com.tangping.androidpractice.model.memorize.QuestionDeck
import com.tangping.androidpractice.model.memorize.RecallStatus
import com.tangping.androidpractice.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryRecallViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val JSON_FILE_NAME = "question_deck.json"
    }

    var viewStates by mutableStateOf(MemoryRecallViewState())
        private set

    private val _viewEvents = Channel<MemoryRecallViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: MemoryRecallViewAction, context: Context) {
        when (action) {
            MemoryRecallViewAction.ChangeCard -> {
                changeCard(context)
            }
            MemoryRecallViewAction.ClickUnfamiliar -> {
                viewStates.currentCard?.updateDueTime(RecallStatus.UNFAMILIAR)
                displayMessage(context.getString(R.string.unfamiliar_toast))
            }
            MemoryRecallViewAction.ClickHesitated -> {
                viewStates.currentCard?.updateDueTime(RecallStatus.HESITATED)
                displayMessage(context.getString(R.string.hesitated_toast))
            }
            MemoryRecallViewAction.ClickRecalled -> {
                viewStates.currentCard?.updateDueTime(RecallStatus.RECALLED)
                displayMessage(context.getString(R.string.recalled_toast))
            }
            is MemoryRecallViewAction.UseRemoteData -> {
                useRemoteData(context, action.url)
            }
        }
    }

    private fun changeCard(context: Context) {
        viewStates.apply {
            val nextDueCard = questionDeck.getNextDueCard()
            viewStates = if (nextDueCard != null) {
                viewStates.copy(currentCard = nextDueCard)
            } else {
                displayMessage(context.getString(R.string.recall_completed))
                viewStates.copy(currentCard = null)
            }
        }
    }

    private fun displayMessage(message: String) {
        viewModelScope.launch {
            _viewEvents.send(MemoryRecallViewEvent.DisplayMessage(message))
        }
    }

    private fun useRemoteData(context: Context, url: String) {
        viewModelScope.launch {
            NetworkUtils.downloadAndSaveJson(context, url, JSON_FILE_NAME)
        }
    }
}

data class MemoryRecallViewState(
    val questionDeck: QuestionDeck = QuestionDeck(
        cards = mutableListOf(
            QuestionCard(question = "1 + 1", answer = "2"),
            QuestionCard(question = "2 x 2", answer = "4"),
            QuestionCard(question = "3 / 3", answer = "1"),
            QuestionCard(question = "4 ^ 4", answer = "256")
        )
    ),
    var currentCard: QuestionCard? = null
)

sealed class MemoryRecallViewEvent {
    data class DisplayMessage(val message: String) : MemoryRecallViewEvent()
}

sealed class MemoryRecallViewAction {
    object ChangeCard : MemoryRecallViewAction()
    object ClickUnfamiliar : MemoryRecallViewAction()
    object ClickHesitated : MemoryRecallViewAction()
    object ClickRecalled : MemoryRecallViewAction()
    data class UseRemoteData(val url: String) : MemoryRecallViewAction()
}