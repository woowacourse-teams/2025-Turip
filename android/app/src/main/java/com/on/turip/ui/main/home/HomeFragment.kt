package com.on.turip.ui.main.home

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.on.turip.R
import com.on.turip.databinding.FragmentHomeBinding
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.search.result.SearchResultActivity
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.provideFactory() }

    private val regionAdapter: RegionAdapter =
        RegionAdapter { regionCategoryName: String ->
            val intent = SearchResultActivity.newIntent(requireContext(), regionCategoryName)
            startActivity(intent)
        }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupTextHighlighting()
        setupAdapters()
        setupObservers()
        setupListeners()
    }

    private fun setupTextHighlighting() {
        val originalText: String = getString(R.string.home_where_should_we_go_title)
        val highlightText: String = getString(R.string.home_where_should_we_go_highlighting)
        val startIndex: Int = originalText.indexOf(highlightText)
        val endIndex: Int = startIndex + highlightText.length

        val spannableText =
            SpannableString(originalText).apply {
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.turip_lemon_faff60_50,
                        ),
                    ),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }

        binding.tvHomeWhereShouldWeGoTitle.text = spannableText
    }

    private fun setupAdapters() {
        binding.rvHomeRegion.adapter = regionAdapter
    }

    private fun setupObservers() {
        viewModel.regionCategories.observe(viewLifecycleOwner) { regionCategories: List<RegionCategory> ->
            regionAdapter.submitList(regionCategories)
        }
        viewModel.isSelectedDomestic.observe(viewLifecycleOwner) { isSelectedDomestic: Boolean ->
            binding.tvHomeDomesticButton.isSelected = isSelectedDomestic
            binding.tvHomeAbroadButton.isSelected = isSelectedDomestic.not()
        }
    }

    private fun setupListeners() {
        binding.tvHomeDomesticButton.setOnClickListener {
            viewModel.loadRegionCategories(isKorea = true)
            Timber.d("국내 클릭")
        }
        binding.tvHomeAbroadButton.setOnClickListener {
            viewModel.loadRegionCategories(isKorea = false)
            Timber.d("해외 클릭")
        }
    }
}
