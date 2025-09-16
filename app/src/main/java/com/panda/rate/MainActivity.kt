package com.panda.rate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.panda.rate.databinding.ActivityMainBinding
import com.panda.ratelib.RateConfig
import com.panda.ratelib.RateDialog

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.btnRate.setOnClickListener {
            val config = RateConfig.Builder()
                .setPositiveText(R.string.rate_us)
                .setPositiveColor(R.color.white)
                .setFeedbackEmail("support.boocha@outlock.com")
                .setCancelable(false)
                .setOnRated { stars ->
                    // Xử lý callback, ví dụ log analytics
                    println("User rated: $stars")
                }
                .build()
            RateDialog(this, config).show()
        }
    }
}