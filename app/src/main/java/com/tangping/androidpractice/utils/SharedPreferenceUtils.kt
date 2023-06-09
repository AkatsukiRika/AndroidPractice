package com.tangping.androidpractice.utils

import android.content.Context

object SharedPreferenceUtils {
    private const val TP_SHARED_PREFERENCES = "tp_shared_preferences"
    const val REMOTE_CACHE_ID = "remote_cache_id"

    fun putInt(context: Context, key: String, value: Int) {
        val sharedPreferences = context.getSharedPreferences(TP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String): Int {
        val sharedPreferences = context.getSharedPreferences(TP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, 0)
    }
}