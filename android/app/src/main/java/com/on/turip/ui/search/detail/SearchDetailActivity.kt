package com.on.turip.ui.search.detail

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.on.turip.databinding.ActivitySearchDetailBinding
import com.on.turip.ui.common.base.BaseActivity

class SearchDetailActivity : BaseActivity<SearchDetailViewModel, ActivitySearchDetailBinding>() {
    override val viewModel: SearchDetailViewModel by viewModels()
    override val binding: ActivitySearchDetailBinding by lazy {
        ActivitySearchDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbSearchDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            },
        )
    }
}
