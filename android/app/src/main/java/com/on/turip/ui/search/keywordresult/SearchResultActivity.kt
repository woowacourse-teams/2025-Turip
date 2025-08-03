package com.on.turip.ui.search.keywordresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.on.turip.R
import com.on.turip.databinding.ActivitySearchResultBinding
import com.on.turip.ui.common.base.BaseActivity

class SearchResultActivity : BaseActivity<ActivitySearchResultBinding>() {
    override val binding: ActivitySearchResultBinding by lazy {
        ActivitySearchResultBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupBindings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbSearchResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tbSearchResult.navigationIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.gray_300_5b5b5b,
            ),
        )
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            },
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBindings() {
        binding.svSearchResult.requestFocus()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SearchResultActivity::class.java)
    }
}
