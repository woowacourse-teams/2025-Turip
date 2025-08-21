package com.on.turip.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.on.turip.databinding.FragmentHomeBinding
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.content.Content
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.common.ItemSpaceDecoration
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.search.keywordresult.SearchActivity
import com.on.turip.ui.search.regionresult.RegionResultActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.provideFactory() }

    private val regionAdapter: RegionAdapter =
        RegionAdapter { regionCategoryName: String ->
            Timber.d("지역 선택 : $regionCategoryName")
            val intent: Intent =
                RegionResultActivity.newIntent(requireContext(), regionCategoryName)
            startActivity(intent)
        }

    private val usersLikeContentAdapter: UsersLikeContentAdapter =
        UsersLikeContentAdapter { content: Content ->
            Timber.d("인기 컨텐츠 선택 : ContentId = ${content.id} CreatorId = ${content.creator.id}")
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = requireContext(),
                    contentId = content.id,
                    creatorId = content.creator.id,
                )
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

        setupAdapters()
        setupObservers()
        setupListeners()
        showNetworkError()
    }

    private fun setupAdapters() {
        binding.rvHomeRegion.apply {
            adapter = regionAdapter
            addItemDecoration(ItemSpaceDecoration(end = 12))
        }
        binding.rvUsersLikeContent.apply {
            adapter = usersLikeContentAdapter
            addItemDecoration(ItemSpaceDecoration(end = 10))
        }
    }

    private fun setupObservers() {
        viewModel.regionCategories.observe(viewLifecycleOwner) { regionCategories: List<RegionCategory> ->
            regionAdapter.submitList(regionCategories)
        }
        viewModel.isSelectedDomestic.observe(viewLifecycleOwner) { isSelectedDomestic: Boolean ->
            binding.tvHomeDomesticButton.isSelected = isSelectedDomestic
            binding.tvHomeAbroadButton.isSelected = isSelectedDomestic.not()
        }
        viewModel.usersLikeContents.observe(viewLifecycleOwner) { usersLikeContents: List<UsersLikeContentModel> ->
            usersLikeContentAdapter.submitList(usersLikeContents)
        }
        viewModel.networkError.observe(viewLifecycleOwner) { networkError: Boolean ->
            binding.gpHomeErrorNot.visibility =
                if (networkError) View.GONE else View.VISIBLE
            binding.customErrorView.visibility =
                if (networkError) View.VISIBLE else View.GONE
        }
        viewModel.serverError.observe(viewLifecycleOwner) { serverError: Boolean ->
            binding.gpHomeErrorNot.visibility =
                if (serverError) View.GONE else View.VISIBLE
            binding.customErrorView.visibility =
                if (serverError) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        binding.tvHomeDomesticButton.setOnClickListener {
            Timber.d("국내 클릭")
            viewModel.loadRegionCategories(isDomestic = true)
        }
        binding.tvHomeAbroadButton.setOnClickListener {
            Timber.d("해외 클릭")
            viewModel.loadRegionCategories(isDomestic = false)
        }
        binding.ivHomeSearch.setOnClickListener {
            if (binding.etHomeSearchResult.text.isBlank()) return@setOnClickListener
            val input: String = binding.etHomeSearchResult.text.toString()
            Timber.d("검색 버튼 클릭 $input")
            val intent: Intent =
                SearchActivity.newIntent(
                    requireContext(),
                    binding.etHomeSearchResult.text.toString(),
                )
            startActivity(intent)
        }
        binding.etHomeSearchResult.setOnEditorActionListener { input, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val input: String = input.text.toString()
                return@setOnEditorActionListener if (input.isBlank()) {
                    true
                } else {
                    val input: String = binding.etHomeSearchResult.text.toString()
                    Timber.d("검색 버튼 클릭 $input")
                    val intent: Intent =
                        SearchActivity.newIntent(
                            requireContext(),
                            input,
                        )
                    startActivity(intent)
                    true
                }
            } else {
                false
            }
        }
    }

    private fun showNetworkError() {
        binding.customErrorView.apply {
            visibility = View.VISIBLE
            setupError(ErrorEvent.NETWORK_ERROR)
            setOnRetryClickListener {
                viewModel.reload()
            }
        }
    }
}
