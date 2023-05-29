package com.tangping.androidpractice.model.memorize

data class QuestionCard(
    val question: String,
    val answer: String,
    var dueTime: Long = System.currentTimeMillis()
) {
    companion object {
        const val INTERVAL_UNFAMILIAR = 1 * 60 * 1000L              // 1 Minute
        const val INTERVAL_HESITATED = 10 * 60 * 1000L              // 10 Minutes
        const val INTERVAL_RECALLED = 3 * 24 * 60 * 60 * 1000L      // 3 Days
    }

    fun updateDueTime(recallStatus: RecallStatus) {
        val interval = when (recallStatus) {
            RecallStatus.UNFAMILIAR -> INTERVAL_UNFAMILIAR
            RecallStatus.HESITATED -> INTERVAL_HESITATED
            RecallStatus.RECALLED -> INTERVAL_RECALLED
        }
        dueTime += interval
    }

    fun isDue() = System.currentTimeMillis() > dueTime
}

enum class RecallStatus {
    UNFAMILIAR, HESITATED, RECALLED
}

data class QuestionDeck(
    private val cards: MutableList<QuestionCard> = mutableListOf()
) {
    fun addCard(card: QuestionCard) {
        cards.add(card)
    }

    fun getNextDueCard() = cards.find { it.isDue() }
}