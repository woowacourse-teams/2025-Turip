package com.on.turip.ui.searchResult

import androidx.activity.viewModels
import com.on.turip.databinding.ActivitySearchResultBinding
import com.on.turip.ui.common.base.BaseActivity

class SearchResultActivity : BaseActivity<SearchResultViewModel, ActivitySearchResultBinding>() {
    override val viewModel: SearchResultViewModel by viewModels()
    override val binding: ActivitySearchResultBinding by lazy {
        ActivitySearchResultBinding.inflate(layoutInflater)
    }
}
