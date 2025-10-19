package com.soundx.util

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray

object SearchHistoryManager {
    private const val PREF_NAME = "search_history"
    private const val KEY_HISTORY = "history"
    private const val MAX_HISTORY_SIZE = 12

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveQuery(context: Context, query: String) {
        val currentQueries = getQueries(context).toMutableList()
        currentQueries.remove(query)
        currentQueries.add(0, query)

        if (currentQueries.size > MAX_HISTORY_SIZE) currentQueries.removeAt(currentQueries.lastIndex)

        val json = JSONArray(currentQueries).toString()
        getPrefs(context).edit { putString(KEY_HISTORY, json) }
    }

    fun deleteQuery(context: Context, query: String) {
        val currentQueries = getQueries(context).toMutableList()
        currentQueries.remove(query)
        val json = JSONArray(currentQueries).toString()
        getPrefs(context).edit { putString(KEY_HISTORY, json) }
    }

    fun getQueries(context: Context): List<String> {
        val json = getPrefs(context).getString(KEY_HISTORY, "[]") ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

}