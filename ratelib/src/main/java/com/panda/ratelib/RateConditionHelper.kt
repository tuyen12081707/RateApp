package com.panda.ratelib

import android.content.Context

object RateConditionHelper {

    /**
     * Kiểm tra xem có nên hiển thị rate dialog hay không dựa trên số lần thực hiện event.
     * * @param context Context
     * @param eventKey Key duy nhất cho action (vd: "out_app", "highlight_view")
     * @param configString Chuỗi remote config (vd: "1,3,5,7" hoặc "1_3_5_7")
     * @param autoIncrement Tự động tăng biến đếm mỗi khi gọi hàm này (mặc định là true)
     */
    fun shouldShowRate(
        context: Context,
        eventKey: String,
        configString: String?,
        autoIncrement: Boolean = true
    ): Boolean {
        if (configString.isNullOrBlank()) return false

        val prefs = RatePreferences(context)

        if (prefs.isRated) return false

        // 2. Tăng biến đếm số lần thực hiện event
        val currentCount = if (autoIncrement) {
            prefs.incrementEventCount(eventKey)
        } else {
            prefs.getEventCount(eventKey)
        }

        val targetCounts = parseConfigString(configString)

        return targetCounts.contains(currentCount)
    }

    fun incrementEventCount(context: Context, eventKey: String) {
        val prefs = RatePreferences(context)
        if (prefs.isRated) return
        prefs.incrementEventCount(eventKey)
    }


    /**
     * Parse chuỗi cấu hình hỗ trợ cả dấu phẩy (,) và dấu gạch dưới (_)
     */

    fun setRatedStatus(context: Context, status: Boolean) {
        RatePreferences(context).isRated = status
    }

    private fun parseConfigString(config: String): List<Int> {
        return try {
            // Sử dụng Regex [,_] để cắt chuỗi theo cả dấu , hoặc _
            config.split(Regex("[,_]"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toIntOrNull() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isRated(context: Context): Boolean {
        return RatePreferences(context).isRated
    }
}