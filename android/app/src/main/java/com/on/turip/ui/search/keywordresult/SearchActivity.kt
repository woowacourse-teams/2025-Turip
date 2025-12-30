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
import com.on.turip.data.common.ErrorUiState
import com.on.turip.databinding.ActivitySearchBinding
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private val viewModel: SearchViewModel by viewModels()

    override val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val searchAdapter: SearchAdapter =
        SearchAdapter { contentId: Long ->
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = this,
                    contentId = contentId,
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
    }

    private fun setupAdapters() {
        binding.rvSearchResult.apply {
            adapter = searchAdapter
            itemAnimator = null
        }
        binding.rvSearchResultSearchHistory.apply {
            adapter = searchHistoryAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@SearchActivity, LinearLayout.VERTICAL))
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbSearchResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tbSearchResult.navigationIcon?.setTint(
            ContextCompat.getColor(this, R.color.gray_300_5b5b5b),
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
                val text: String = input.text.toString()
                return@setOnEditorActionListener if (text.isBlank()) {
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
            if (hasFocus && viewModel.uiState.value !is SearchUiState.Error) {
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

        viewModel.searchHistory.observe(this) { searchHistories: List<SearchHistory> ->
            searchHistoryAdapter.submitList(searchHistories)
        }

        collectOnStarted(viewModel.uiState) { uiState: SearchUiState ->
            when (uiState) {
                SearchUiState.Loading -> renderLoading()
                SearchUiState.Empty -> renderEmptyView()
                is SearchUiState.Success -> renderContents(uiState)
                is SearchUiState.Error -> renderErrorView(uiState.errorUiState)
            }
        }

        collectOnStarted(viewModel.commonUiEffect) { commonUiEffect: CommonUiEffect ->
            when (commonUiEffect) {
                CommonUiEffect.NavigateToLogin -> navigateToLoginScreen()
            }
        }
    }

    private fun renderLoading() {
        binding.pbSearch.visibility = View.VISIBLE
        binding.groupSearchResultNotEmpty.visibility = View.GONE
        binding.groupSearchResultEmpty.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun renderEmptyView() {
        binding.groupSearchResultEmpty.visibility = View.VISIBLE
        binding.groupSearchResultNotEmpty.visibility = View.GONE
        binding.pbSearch.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
        binding.rvSearchResultSearchHistory.visibility = View.GONE
    }

    private fun renderContents(uiState: SearchUiState.Success) {
        binding.groupSearchResultEmpty.visibility = View.GONE
        binding.groupSearchResultNotEmpty.visibility = View.VISIBLE
        binding.pbSearch.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
        binding.rvSearchResultSearchHistory.visibility = View.GONE

        searchAdapter.submitList(uiState.videos)
        binding.tvSearchResultCount.text =
            getString(R.string.search_result_exist_result, uiState.totalCount)
    }

    private fun renderErrorView(errorUiState: ErrorUiState) {
        binding.groupSearchResultNotEmpty.visibility = View.GONE
        binding.groupSearchResultEmpty.visibility = View.GONE
        binding.pbSearch.visibility = View.GONE
        binding.rvSearchResultSearchHistory.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            render(errorUiState)
            setOnRetryClickListener { viewModel.loadByKeyword() }
        }
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(this)
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        finish()
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
        const val SEARCH_KEYWORD_KEY: String = "com.on.turip.SEARCH_KEYWORD_KEY"

        fun newIntent(
            context: Context,
            searchKeyword: String,
        ): Intent =
            Intent(context, SearchActivity::class.java).apply {
                putExtra(SEARCH_KEYWORD_KEY, searchKeyword)
            }
    }
}
