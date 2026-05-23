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
        // 1. Nếu chuỗi config rỗng hoặc null -> không hiển thị
        if (configString.isNullOrBlank()) return false

        val prefs = RatePreferences(context)

        // 2. Tăng biến đếm số lần thực hiện event
        val currentCount = if (autoIncrement) {
            prefs.incrementEventCount(eventKey)
        } else {
            prefs.getEventCount(eventKey)
        }

        // 3. Parse chuỗi config thành List<Int> (hỗ trợ cả dấu , và dấu _)
        val targetCounts = parseConfigString(configString)

        // 4. Kiểm tra xem lần hiện tại có trùng với các mốc trong cấu hình không
        return targetCounts.contains(currentCount)
    }

    /**
     * Parse chuỗi cấu hình hỗ trợ cả dấu phẩy (,) và dấu gạch dưới (_)
     */
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
}