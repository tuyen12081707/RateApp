package com.panda.rate

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.panda.rate.databinding.ActivityMainBinding

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