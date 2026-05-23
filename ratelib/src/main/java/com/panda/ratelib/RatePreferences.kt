package com.panda.ratelib

import android.content.Context
import android.content.SharedPreferences

internal class RatePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("panda_rate_prefs", Context.MODE_PRIVATE)

    var isRated: Boolean
        get() = prefs.getBoolean("is_rated", false)
        set(value) = prefs.edit().putBoolean("is_rated", value).apply()

    fun getEventCount(eventKey: String): Int {
        return prefs.getInt("count_$eventKey", 0)
    }

    fun incrementEventCount(eventKey: String): Int {
        val current = getEventCount(eventKey)
        val next = current + 1
        prefs.edit().putInt("count_$eventKey", next).apply()
        return next
    }
}