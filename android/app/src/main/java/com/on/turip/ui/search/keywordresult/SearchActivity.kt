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
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import com.on.turip.R
import com.on.turip.databinding.ActivitySearchBinding
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.search.model.VideoInformationModel
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private val viewModel: SearchViewModel by viewModels {
        val searchKeyword: String = intent.getStringExtra(SEARCH_KEYWORD_KEY) ?: ""
        SearchViewModel.provideFactory(
            searchKeyword = searchKeyword,
        )
    }

    override val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val searchAdapter: SearchAdapter =
        SearchAdapter { contentId: Long, creatorId: Long ->
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = this,
                    contentId = contentId,
                    creatorId = creatorId,
                )
            startActivity(intent)
        }

    private val searchHistoryAdapter: SearchHistoryAdapter =
        SearchHistoryAdapter(
            object : SearchHistoryViewHolder.SearchHistoryListener {
                override fun onSearchHistoryDeleteClick(keyword: String) {
                    viewModel.deleteSearchHistory(keyword)
                }

                override fun onSearchHistoryItemClick(keyword: String) {
                    binding.etSearchResult.setText(keyword)
                    binding.etSearchResult.setSelection(keyword.length)

                    viewModel.updateSearchingWord(keyword)
                    viewModel.loadByKeyword()
                    viewModel.createSearchHistory()
                    binding.rvSearchResultSearchHistory.visibility = View.GONE
                    binding.rvSearchResult.scrollToPosition(0)
                }
            },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupListeners()
        setupObservers()
        setupAdapters()
        setupOnBackPressedDispatcher()
        showNetworkError()
    }

    private fun showNetworkError() {
        binding.customErrorView.apply {
            visibility = View.VISIBLE
            setupError(ErrorEvent.NETWORK_ERROR)
            setOnRetryClickListener {
                viewModel.loadByKeyword()
            }
        }
    }

    private fun setupAdapters() {
        binding.rvSearchResult.adapter = searchAdapter
        binding.rvSearchResultSearchHistory.adapter = searchHistoryAdapter
        binding.rvSearchResult.itemAnimator = null
        binding.rvSearchResultSearchHistory.itemAnimator = null
        binding.rvSearchResultSearchHistory.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayout.VERTICAL,
            ),
        )
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

    private fun setupOnBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.rvSearchResultSearchHistory.isVisible) {
                        binding.rvSearchResultSearchHistory.visibility = View.GONE
                        binding.etSearchResult.clearFocus()
                    } else {
                        finish()
                    }
                }
            },
        )
    }

    private fun setupListeners() {
        binding.etSearchResult.addTextChangedListener { editable: Editable? ->
            viewModel.updateSearchingWord(editable.toString())
        }
        binding.etSearchResult.setOnEditorActionListener { input, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val input: String = input.text.toString()
                return@setOnEditorActionListener if (input.isBlank()) {
                    true
                } else {
                    viewModel.loadByKeyword()
                    viewModel.createSearchHistory()
                    binding.rvSearchResultSearchHistory.visibility = View.GONE
                    binding.etSearchResult.clearFocus()
                    hideKeyBoard(binding.etSearchResult)
                    binding.rvSearchResult.scrollToPosition(0)
                    Timber.d("검색창 클릭")
                    true
                }
            } else {
                false
            }
        }

        binding.etSearchResult.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && viewModel.serverError.value?.not() == true && viewModel.networkError.value?.not() == true) {
                Timber.d("검색창 포커싱")
                binding.rvSearchResultSearchHistory.visibility = View.VISIBLE
            }
        }

        binding.ivSearchResultClear.setOnClickListener {
            binding.etSearchResult.text.clear()
            binding.etSearchResult.requestFocus()
            showKeyBoard(binding.etSearchResult)
            Timber.d("최근 검색 목록에서 삭제 버튼 클릭")
        }
        binding.clSearch.setOnClickListener {
            binding.etSearchResult.requestFocus()
            showKeyBoard(binding.etSearchResult)
            Timber.d("검색창 포커싱")
        }
    }

    private fun showKeyBoard(editText: EditText) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupObservers() {
        viewModel.searchingWord.observe(this) { searchWord: String ->
            binding.ivSearchResultClear.visibility =
                if (searchWord.isNotEmpty()) View.VISIBLE else View.GONE
            if (binding.etSearchResult.text.toString() != searchWord) {
                binding.etSearchResult.setText(searchWord)
                binding.etSearchResult.setSelection(searchWord.length)
            }
        }
        viewModel.videoInformation.observe(this) { videoInformationModels: List<VideoInformationModel> ->
            searchAdapter.submitList(videoInformationModels)
        }
        viewModel.searchResultCount.observe(this) { searchResultCount: Int ->
            binding.tvSearchResultCount.text =
                getString(R.string.search_result_exist_result, searchResultCount)
            handleVisibleBySearchResult(searchResultCount)
        }
        viewModel.loading.observe(this) { loading: Boolean ->
            handleVisibleByLoading(loading)
        }
        viewModel.searchHistory.observe(this) { searchHistories: List<SearchHistory> ->
            searchHistoryAdapter.submitList(searchHistories)
        }
        viewModel.serverError.observe(this) { serverError ->
            handleVisibleByError(serverError)
        }
        viewModel.networkError.observe(this) { networkError ->
            handleVisibleByError(networkError)
        }
    }

    private fun handleVisibleBySearchResult(searchResultCount: Int) {
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

    private fun handleVisibleByLoading(loading: Boolean) {
        if (loading) {
            binding.pbSearchResult.visibility = View.VISIBLE
            binding.tvSearchResultCount.visibility = View.GONE
            binding.rvSearchResult.visibility = View.GONE
            binding.groupSearchResultEmpty.visibility = View.GONE
            binding.customErrorView.visibility = View.GONE
        } else {
            binding.pbSearchResult.visibility = View.GONE
            binding.tvSearchResultCount.visibility = View.VISIBLE
            binding.rvSearchResult.visibility = View.VISIBLE
        }
    }

    private fun handleVisibleByError(error: Boolean) {
        if (error) {
            binding.customErrorView.visibility = View.VISIBLE
            binding.tvSearchResultCount.visibility = View.GONE
            binding.rvSearchResult.visibility = View.GONE
            binding.groupSearchResultEmpty.visibility = View.GONE
            binding.rvSearchResultSearchHistory.visibility = View.GONE
        } else {
            binding.customErrorView.visibility = View.GONE
            binding.tvSearchResultCount.visibility = View.VISIBLE
            binding.rvSearchResult.visibility = View.VISIBLE
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect: Rect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyBoard(v)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyBoard(editText: EditText) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    companion object {
        private const val SEARCH_KEYWORD_KEY: String = "com.on.turip.SEARCH_KEYWORD_KEY"

        fun newIntent(
            context: Context,
            searchKeyword: String,
        ): Intent =
            Intent(context, SearchActivity::class.java).apply {
                putExtra(SEARCH_KEYWORD_KEY, searchKeyword)
            }
    }
}
