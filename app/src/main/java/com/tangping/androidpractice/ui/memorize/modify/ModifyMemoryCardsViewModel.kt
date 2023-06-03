package com.tangping.androidpractice.ui.memorize.modify

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tangping.androidpractice.model.memorize.QuestionCard
import com.tangping.androidpractice.utils.JsonUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
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
            is ModifyMemoryCardsAction.ChangeQuestion -> {
                changeQuestion(action.question)
            }
            is ModifyMemoryCardsAction.ChangeAnswer -> {
                changeAnswer(action.answer)
            }
            is ModifyMemoryCardsAction.AddNewEntry -> {
                addNewEntry(action.index)
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
            currentCard = viewStates.questionCards[index],
            currentIndex = index
        )
    }

    private fun changeQuestion(question: String) {
        val currentCard = viewStates.currentCard?.copy(question = question)
        viewStates = viewStates.copy(
            currentCard = currentCard
        )
    }

    private fun changeAnswer(answer: String) {
        val currentCard = viewStates.currentCard?.copy(answer = answer)
        viewStates = viewStates.copy(
            currentCard = currentCard
        )
    }

    private fun addNewEntry(index: Int? = null) {
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
            currentIndex = if (index == null) viewStates.currentIndex + 1 else viewStates.currentIndex
        )
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
    var currentIndex: Int = 0
)

sealed class ModifyMemoryCardsEvent {
}

sealed class ModifyMemoryCardsAction {
    data class ReadJson(val fileName: String) : ModifyMemoryCardsAction()

    data class SetIndex(val index: Int) : ModifyMemoryCardsAction()

    data class ChangeQuestion(val question: String) : ModifyMemoryCardsAction()

    data class ChangeAnswer(val answer: String) : ModifyMemoryCardsAction()

    data class AddNewEntry(val index: Int? = null) : ModifyMemoryCardsAction()
}