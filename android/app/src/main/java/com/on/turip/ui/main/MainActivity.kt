package com.on.turip.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.on.turip.R
import com.on.turip.databinding.ActivityMainBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.model.RegionModel
import com.on.turip.ui.search.result.SearchResultActivity

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel: MainViewModel by viewModels()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val metropolitanCitiesAdapter: RegionAdapter =
        RegionAdapter { region: RegionModel ->
            val intent = SearchResultActivity.newIntent(this@MainActivity, region.english)
            startActivity(intent)
        }
    private val provincesAdapter: RegionAdapter =
        RegionAdapter { region: RegionModel ->
            val intent = SearchResultActivity.newIntent(this@MainActivity, region.english)
            startActivity(intent)
        }

    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTextHighlighting()
        handleDoubleBackPressToExit()
        setupAdapters()
        setupObservers()
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

    private fun handleDoubleBackPressToExit() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime <= 2000) {
                        finish()
                    } else {
                        backPressedTime = System.currentTimeMillis()
                        Toast
                            .makeText(
                                this@MainActivity,
                                getString(R.string.main_double_back_pressed_to_exit),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }
            },
        )
    }

    private fun setupAdapters() {
        binding.rvMainMetropolitanCity.adapter = metropolitanCitiesAdapter
        binding.rvMainProvince.adapter = provincesAdapter
    }

    private fun setupObservers() {
        viewModel.metropolitanCities.observe(this) { metropolitanCities: List<RegionModel> ->
            metropolitanCitiesAdapter.submitList(metropolitanCities)
        }

        viewModel.provinces.observe(this) { provinces: List<RegionModel> ->
            provincesAdapter.submitList(provinces)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
