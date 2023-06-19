package com.tangping.androidpractice.ui.memorize

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tangping.androidpractice.R
import com.tangping.androidpractice.model.memorize.QuestionCard
import com.tangping.androidpractice.model.memorize.QuestionDeck
import com.tangping.androidpractice.model.memorize.RecallStatus
import com.tangping.androidpractice.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MemoryRecallViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val TAG = "MemoryRecallViewModel"
        const val JSON_FILE_NAME = "question_deck.json"
        const val KEY_QUESTION_DECK = "question_deck"
        const val KEY_CARDS = "cards"
        const val KEY_QUESTION = "question"
        const val KEY_ANSWER = "answer"
        const val KEY_DUE_TIME = "due_time"
    }

    var viewStates by mutableStateOf(MemoryRecallViewState())
        private set

    private val _viewEvents = Channel<MemoryRecallViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: MemoryRecallViewAction, context: Context) {
        when (action) {
            MemoryRecallViewAction.ChangeCard -> {
                changeCard(context)
                viewModelScope.launch {
                    _viewEvents.send(MemoryRecallViewEvent.HideAnswer)
                }
            }
            MemoryRecallViewAction.ClickUnfamiliar -> {
                viewStates.currentCard?.updateDueTime(RecallStatus.UNFAMILIAR)
                refreshDueTime()
            }
            MemoryRecallViewAction.ClickHesitated -> {
                viewStates.currentCard?.updateDueTime(RecallStatus.HESITATED)
                refreshDueTime()
            }
            MemoryRecallViewAction.ClickRecalled -> {
                viewStates.currentCard?.updateDueTime(RecallStatus.RECALLED)
                refreshDueTime()
            }
            is MemoryRecallViewAction.UseRemoteData -> {
                useRemoteData(context, action.url)
            }
            is MemoryRecallViewAction.UseLocalCache -> {
                useLocalCache(context, action.fileName)
            }
            is MemoryRecallViewAction.SaveQuestionDeck -> {
                saveQuestionDeck(context, action.fileName)
            }
        }
    }

    private fun changeCard(context: Context) {
        val nextDueCard = viewStates.questionDeck.getNextDueCard()
        Log.i(TAG, "nextDueCard=$nextDueCard, question=${nextDueCard?.question}, cardCount=${viewStates.questionDeck.getCardCount()}")
        viewStates = MemoryRecallViewState(
            questionDeck = viewStates.questionDeck,
            currentCard = nextDueCard
        )
        if (nextDueCard == null) {
            displayMessage(context.getString(R.string.recall_completed))
        }
    }

    private fun displayMessage(message: String) {
        viewModelScope.launch {
            _viewEvents.send(MemoryRecallViewEvent.DisplayMessage(message))
        }
    }

    private fun refreshDueTime() {
        viewModelScope.launch {
            _viewEvents.send(MemoryRecallViewEvent.RefreshDueTime)
        }
    }

    private fun useRemoteData(context: Context, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val saveResult = NetworkUtils.downloadAndSaveJson(context, url, JSON_FILE_NAME)
                if (saveResult) {
                    updateQuestionDeck(context)
                    _viewEvents.send(MemoryRecallViewEvent.DismissPopup)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun useLocalCache(context: Context, fileName: String? = null) {
        if (File(context.cacheDir, fileName ?: JSON_FILE_NAME).exists().not()) {
            displayMessage(context.getString(R.string.no_local_cache))
            return
        }
        viewModelScope.launch(Dispatchers.Main) {
            updateQuestionDeck(context, fileName)
            _viewEvents.send(MemoryRecallViewEvent.DismissPopup)
        }
    }

    private fun updateQuestionDeck(context: Context, fileName: String? = null) {
        val jsonFile = File(context.cacheDir, fileName ?: JSON_FILE_NAME)
        try {
            val jsonString = jsonFile.bufferedReader().use {
                it.readText()
            }
            val jsonObject = JSONObject(jsonString)
            val questionDeck = jsonObject.getJSONObject(KEY_QUESTION_DECK)
            val cards = questionDeck.getJSONArray(KEY_CARDS)
            val questionCards = mutableListOf<QuestionCard>()
            for (index in 0 until cards.length()) {
                val card = cards[index] as JSONObject
                val question = card.getString(KEY_QUESTION)
                val answer = card.getString(KEY_ANSWER)
                val dueTime = card.optLong(KEY_DUE_TIME, System.currentTimeMillis())
                val questionCard = QuestionCard(question, answer, dueTime)
                questionCards.add(questionCard)
            }
            viewStates = viewStates.copy(
                questionDeck = QuestionDeck(
                    cards = questionCards
                )
            )
            Log.i(TAG, "updateQuestionDeck, cardCount=${viewStates.questionDeck.getCardCount()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveQuestionDeck(context: Context, fileName: String? = null) {
        val jsonFile = File(context.cacheDir, fileName ?: JSON_FILE_NAME)
        try {
            val gson = Gson()
            val json = JsonObject()
            json.add(KEY_QUESTION_DECK, gson.toJsonTree(viewStates.questionDeck))
            val outputStreamWriter = OutputStreamWriter(FileOutputStream(jsonFile))
            outputStreamWriter.write(json.toString())
            outputStreamWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
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
    object DismissPopup : MemoryRecallViewEvent()
    object RefreshDueTime : MemoryRecallViewEvent()
    object HideAnswer : MemoryRecallViewEvent()
}

sealed class MemoryRecallViewAction {
    object ChangeCard : MemoryRecallViewAction()
    object ClickUnfamiliar : MemoryRecallViewAction()
    object ClickHesitated : MemoryRecallViewAction()
    object ClickRecalled : MemoryRecallViewAction()
    data class UseRemoteData(val url: String) : MemoryRecallViewAction()
    data class UseLocalCache(var fileName: String? = null) : MemoryRecallViewAction()
    data class SaveQuestionDeck(var fileName: String? = null) : MemoryRecallViewAction()
}