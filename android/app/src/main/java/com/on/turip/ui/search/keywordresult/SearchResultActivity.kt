package com.on.turip.ui.search.keywordresult

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
        binding.etSearchResult.requestFocus()
        binding.etSearchResult.setOnFocusChangeListener { _, hasFocus ->
            binding.ivSearchResultSearch.isVisible = !hasFocus
        }
        binding.ivSearchResultSearch.isVisible = !binding.etSearchResult.hasFocus()
        binding.etSearchResult.addTextChangedListener {
            binding.ivSearchResultClear.isVisible = it?.isNotBlank() == true
        }
        binding.ivSearchResultClear.setOnClickListener {
            binding.etSearchResult.text.clear()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SearchResultActivity::class.java)
    }
}
