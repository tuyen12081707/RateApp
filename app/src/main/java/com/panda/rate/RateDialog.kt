package com.panda.rate

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.panda.rate.databinding.PopupRatingBinding
import androidx.core.graphics.drawable.toDrawable
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics


class RateDialog(
    private val context: Context,
    private val config: RateConfig
) : Dialog(context, R.style.RateDialog) {
    private var starViews: List<ImageView> = emptyList()
    private var selectedStars = 0
    private val binding: PopupRatingBinding by lazy {
        PopupRatingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(config.cancelable)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setDimAmount(0.5f)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        init()
    }

    private fun init() {
        binding.btnRate.text = context.getString(config.positiveText)
        config.positiveColor?.let {
            binding.btnRate.setTextColor(
                ContextCompat.getColor(
                    context,
                    it
                )
            )
        }

        binding.btnClose.setOnClickListener {
            logEvent("rate_cancel")
            dismiss()
        }
        setupStars(binding)
        starViews.forEachIndexed { index, star ->
            star.setOnClickListener {
                updateStars(binding, index + 1)
            }
        }

        binding.btnRate.setOnClickListener {
            if (selectedStars >= 4) {
                showInAppReview(activity = context as Activity)
            } else {
                sendFeedback(context, selectedStars, config.feedbackEmail)
            }
            logEvent("rate_submit", bundleOf("rate_star" to selectedStars))
            config.onRated?.invoke(selectedStars)
            dismiss()
        }

    }

    override fun show() {
        super.show()
        logEvent("rate_show")
    }

    fun logEvent(event: String, bundle: Bundle? = null) {
        Log.d("event==", event)
        try {
            Firebase.analytics.logEvent(event, bundle)
        } catch (_: Exception) {
        }
    }

    private fun setupStars(binding: PopupRatingBinding) {
        val starCount = 5
        val container = binding.starContainer
        starViews = (0 until starCount).map { i ->
            ImageView(container.context).apply {
                layoutParams = LinearLayout.LayoutParams(0, dpToPx(45)).apply {
                    weight = 1f
                    if (i != 0) marginStart = dpToPx(2)
                }
                setImageResource(R.drawable.ic_start_not_active)
                adjustViewBounds = true
                setOnClickListener {
                    updateStars(binding, i + 1)
                }
                container.addView(this)
            }
        }
    }

    private fun updateStars(binding: PopupRatingBinding, stars: Int) {
        selectedStars = stars
        starViews.forEachIndexed { index, star ->
            star.setImageResource(
                if (index < stars) R.drawable.ic_star_active
                else R.drawable.ic_start_not_active
            )
        }

        binding.btnRate.isEnabled = stars > 0
        binding.btnRate.alpha = if (stars > 0) 1f else 0.5f

        val emojis = listOf(
            R.drawable.emoji1,
            R.drawable.emoji2,
            R.drawable.emoji3,
            R.drawable.emoji4,
            R.drawable.emoji5
        )
        val labels = listOf(
            R.string.text_rate1,
            R.string.text_rate2,
            R.string.text_rate3,
            R.string.text_rate4,
            R.string.text_rate5
        )

        binding.rateEmotion.setImageResource(emojis[stars - 1])
        binding.ratingLbl.setText(labels[stars - 1])
        binding.btnRate.text = if (stars < 4) binding.root.context.getString(R.string.rate_us)
        else binding.root.context.getString(R.string.rate_on_google_play)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * Resources.getSystem().displayMetrics.density).toInt()


    private fun showInAppReview(activity: Activity) {
        val manager = if (BuildConfig.DEBUG) {
            FakeReviewManager(activity)
        } else {
            ReviewManagerFactory.create(activity)
        }
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = reviewInfo?.let { manager.launchReviewFlow(activity, it) }
                flow?.addOnCompleteListener { _ ->
                }
            } else {

                try {
                    val open = Intent(Intent.ACTION_VIEW)
                    open.data =
                        ("https://play.google.com/store/apps/details?id=" + activity.packageName).toUri()
                    activity.startActivity(open)
                } catch (_: Exception) {

                }

            }
        }
        request.addOnFailureListener {
            try {
                val open = Intent(Intent.ACTION_VIEW)
                open.data =
                    ("https://play.google.com/store/apps/details?id=" + activity.packageName).toUri()
                activity.startActivity(open)
            } catch (_: Exception) {

            }

        }
    }

    private fun sendFeedback(context: Context, stars: Int, email: String) {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // chuẩn cho email
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    context.getString(R.string.app_name)
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    "+ ${context.getString(R.string.app_name)}\n" +
                            "+ ${context.getString(R.string.rate_stars)} ($stars/5)\n" +
                            "+ ${context.getString(R.string.feedback)}"
                )
            }
            context.startActivity(
                Intent.createChooser(
                    emailIntent,
                    context.getString(R.string.feedback)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}