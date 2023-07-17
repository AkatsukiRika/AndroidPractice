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
import kotlin.Exception

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
        return try {
            val jsonString = jsonFile.bufferedReader().use {
                it.readText()
            }
            readJsonString(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun readJsonString(jsonString: String): List<QuestionCard> {
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

    /**
     * @param localCards 当前QuestionDeck内的卡片列表
     * @param url 远端JSON地址
     * @return 更新后的卡片列表
     *
     * 更新规则：
     * 1. 问题文字相同时，视为同一张卡片。此时，若本地与远端答案相同，不进行更新。若答案不同，使用远端答案，并用当前时间戳重置到期时间；
     * 2. 问题文字不同的卡片，全部加入到本地卡片列表的末尾，到期时间以远端为准；
     * 3. 若某张卡片本地存在，远端不存在，保留本地卡片。
     */
    suspend fun refreshRemoteData(localCards: List<QuestionCard>, url: String): List<QuestionCard> {
        try {
            val jsonString = NetworkUtils.getStringFromUrl(url)
            val remoteCards = readJsonString(jsonString)
            if (localCards.isEmpty()) {
                return remoteCards
            }
            if (remoteCards.isEmpty()) {
                return localCards
            }

            val mergedCards = mutableListOf<QuestionCard>()
            val remoteAccessFlags = BooleanArray(remoteCards.size)
            localCards.forEach { localCard ->
                val remoteIndex = remoteCards.indexOfFirst { it.question == localCard.question }
                if (remoteIndex != -1) {
                    val remoteCard = remoteCards[remoteIndex]
                    if (localCard.answer.equals(remoteCard.answer).not()) {
                        localCard.dueTime = System.currentTimeMillis()
                    }
                    localCard.answer = remoteCard.answer
                    remoteAccessFlags[remoteIndex] = true
                }
                mergedCards.add(localCard)
            }
            for (i in remoteAccessFlags.indices) {
                if (remoteAccessFlags[i].not()) {
                    mergedCards.add(remoteCards[i])
                }
            }
            return mergedCards
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
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