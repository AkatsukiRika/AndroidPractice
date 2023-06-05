package com.tangping.androidpractice.utils

import android.content.Context
import android.util.Log
import com.tangping.androidpractice.model.memorize.QuestionCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception

object JsonUtils {
    private const val TAG = "JsonUtils"
    const val KEY_QUESTION_DECK = "question_deck"
    const val KEY_CARDS = "cards"
    const val KEY_QUESTION = "question"
    const val KEY_ANSWER = "answer"
    const val KEY_DUE_TIME = "due_time"

    fun readJson(context: Context, fileName: String): List<QuestionCard> {
        val jsonFile = File(context.cacheDir, fileName)
        if (jsonFile.exists().not()) {
            Log.i(TAG, "jsonFile(${jsonFile.absolutePath}) does not exist!")
            return emptyList()
        }
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
            return questionCards
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun writeJson(context: Context, fileName: String, questionCards: List<QuestionCard>) {
        val jsonFile = File(context.cacheDir, fileName)
        if (jsonFile.exists().not()) {
            Log.i(TAG, "jsonFile(${jsonFile.absolutePath}) does not exist!")
            return
        }
        try {
            val cards = JSONArray()
            questionCards.forEach {
                val card = JSONObject()
                card.put(KEY_QUESTION, it.question)
                card.put(KEY_ANSWER, it.answer)
                card.put(KEY_DUE_TIME, it.dueTime)
                cards.put(card)
            }
            val deck = JSONObject()
            deck.put(KEY_CARDS, cards)
            val jsonObject = JSONObject()
            jsonObject.put(KEY_QUESTION_DECK, deck)
            val outputStreamWriter = OutputStreamWriter(FileOutputStream(jsonFile))
            outputStreamWriter.write(jsonObject.toString())
            outputStreamWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun scanCacheDirectory(context: Context) =
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
}