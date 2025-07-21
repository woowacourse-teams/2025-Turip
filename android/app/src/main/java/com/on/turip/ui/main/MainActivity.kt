package com.on.turip.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.on.turip.R
import com.on.turip.databinding.ActivityMainBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.model.RegionModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel: MainViewModel by viewModels()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val metropolitanCitiesAdapter: RegionAdapter =
        RegionAdapter(
            object : RegionViewHolder.OnRegionListener {
                override fun onRegionClick(region: RegionModel) {
                    // TODO: 지역 검색 결과 뷰로 이동
                }
            },
        )
    private val provincesAdapter: RegionAdapter =
        RegionAdapter(
            object : RegionViewHolder.OnRegionListener {
                override fun onRegionClick(region: RegionModel) {
                    // TODO: 지역 검색 결과 뷰로 이동
                }
            },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTextHighlighting()
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
