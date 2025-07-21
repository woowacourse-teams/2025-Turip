package com.on.turip.ui.main

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.on.turip.R
import com.on.turip.databinding.ActivityMainBinding
import com.on.turip.ui.common.base.BaseActivity

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel: MainViewModel by viewModels()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTextHighlighting()
    }

    private fun setupTextHighlighting() {
        val originalText: String = getString(R.string.main_where_should_we_go_title)
        val highlightText: String = getString(R.string.main_where_should_we_go_highlighting)
        val startIndex: Int = originalText.indexOf(highlightText)
        val endIndex: Int = startIndex + highlightText.length

        val spannableText =
            SpannableString(originalText).apply {
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.turip_lemon_faff60_50,
                        ),
                    ),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }

        binding.tvMainWhereShouldWeGoTitle.text = spannableText
    }
}
