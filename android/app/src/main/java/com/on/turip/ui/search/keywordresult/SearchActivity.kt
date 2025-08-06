package com.on.turip.ui.search.keywordresult

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.on.turip.R
import com.on.turip.databinding.ActivitySearchBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.search.model.VideoInformationModel
import com.on.turip.ui.trip.detail.TripDetailActivity

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.provideFactory()
    }

    override val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val searchAdapter: SearchAdapter =
        SearchAdapter { contentId: Long?, creatorId: Long? ->
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = this,
                    contentId = contentId ?: 0,
                    creatorId = creatorId ?: 0,
                )
            startActivity(intent)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupListeners()
        setupObserves()
        binding.etSearchResult.requestFocus()
        binding.rvSearchResult.adapter = searchAdapter
        binding.rvSearchResult.itemAnimator = null
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListeners() {
        binding.etSearchResult.addTextChangedListener { editable: Editable? ->
            viewModel.updateSearchingWord(editable)
        }
        binding.etSearchResult.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.updateByKeyword()
                true
            } else {
                false
            }
        }

        binding.ivSearchResultClear.setOnClickListener {
            binding.etSearchResult.text.clear()
        }
    }

    private fun setupObserves() {
        viewModel.searchingWord.observe(this) { words: String ->
            binding.ivSearchResultClear.visibility =
                if (words.isNotBlank()) View.VISIBLE else View.GONE
        }
        viewModel.videoInformation.observe(this) { videoInformationModels: List<VideoInformationModel> ->
            searchAdapter.submitList(videoInformationModels)
        }
        viewModel.searchResultCount.observe(this) { searchResultCount: Int ->
            binding.tvSearchResultCount.text =
                getString(R.string.search_result_exist_result, searchResultCount)
            if (searchResultCount == 0) {
                binding.groupSearchResultEmpty.visibility = View.VISIBLE
                binding.tvSearchResultCount.visibility = View.GONE
                binding.rvSearchResult.visibility = View.GONE
            } else {
                binding.groupSearchResultEmpty.visibility = View.GONE
                binding.tvSearchResultCount.visibility = View.VISIBLE
                binding.rvSearchResult.visibility = View.VISIBLE
            }
        }
        viewModel.loading.observe(this) { loading: Boolean ->
            if (loading) {
                binding.pbSearchResult.visibility = View.VISIBLE
                binding.tvSearchResultCount.visibility = View.GONE
                binding.rvSearchResult.visibility = View.GONE
                binding.groupSearchResultEmpty.visibility = View.GONE
            } else {
                binding.pbSearchResult.visibility = View.GONE
                binding.tvSearchResultCount.visibility = View.VISIBLE
                binding.rvSearchResult.visibility = View.VISIBLE
            }
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
        fun newIntent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }
}
