package com.panda.ratelib

import android.content.Context

object RateConditionHelper {

    /**
     * @param context Context
     * @param eventKey Key duy nhất cho action (vd: "out_app", "highlight_view")
     * @param configString Chuỗi remote config (vd: "1,3,5,7,9")
     * @param autoIncrement Tự động tăng biến đếm khi gọi hàm này (mặc định là true)
     */
    fun shouldShowRate(
        context: Context,
        eventKey: String,
        configString: String?,
        autoIncrement: Boolean = true
    ): Boolean {
        val prefs = RatePreferences(context)

        // 1. Nếu đã rate rồi thì không bao giờ hiển thị lại
        if (prefs.isRated) return false

        // 2. Nếu chuỗi config rỗng hoặc null -> không hiển thị
        if (configString.isNullOrBlank()) return false

        // 3. Tăng biến đếm
        val currentCount = if (autoIncrement) {
            prefs.incrementEventCount(eventKey)
        } else {
            prefs.getEventCount(eventKey)
        }

        // 4. Parse chuỗi config thành List<Int>
        val targetCounts = parseConfigString(configString)

        // 5. Kiểm tra xem lần hiện tại có nằm trong danh sách yêu cầu không
        return targetCounts.contains(currentCount)
    }

    /**
     * Đánh dấu là user đã rate thành công (để không gọi lại nữa)
     */
    fun markAsRated(context: Context) {
        RatePreferences(context).isRated = true
    }

    private fun parseConfigString(config: String): List<Int> {
        return try {
            config.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toIntOrNull() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}