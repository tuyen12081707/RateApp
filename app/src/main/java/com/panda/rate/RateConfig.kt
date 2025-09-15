package com.panda.rate

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

class RateConfig private constructor(
    @StringRes val positiveText: Int,
    @ColorRes val positiveColor: Int?,
    val feedbackEmail: String,
    val cancelable: Boolean,
    @StringRes val cancelLabel: Int,
    val onRated: ((Int) -> Unit)?,
    val onCancelled: (() -> Unit)?
){
    class Builder {
        private var positiveText: Int = R.string.rate_us
        private var positiveColor: Int = R.color.white
        private var feedbackEmail: String = "your_email@example.com"
        private var onRated: ((Int) -> Unit)? = null
        private var cancelable: Boolean = true
        private var onCancelled: (() -> Unit)? = null
        fun setPositiveText(@StringRes text: Int) = apply { this.positiveText = text }
        fun setPositiveColor(@ColorRes color: Int) = apply { this.positiveColor = color }
        fun setFeedbackEmail(email: String) = apply { this.feedbackEmail = email }
        fun setOnRated(listener: (Int) -> Unit) = apply { this.onRated = listener }
        fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        private var cancelLabel: Int = R.string.cancel
        fun setOnCancelled(listener: () -> Unit) = apply { this.onCancelled = listener }
        fun build(): RateConfig {
            val config = RateConfig(
                positiveText = positiveText,
                positiveColor = positiveColor,
                feedbackEmail = feedbackEmail,
                cancelable = cancelable,
                cancelLabel = cancelLabel,
                onRated = onRated,
                onCancelled = onCancelled
            )
            return config
        }
    }
}

