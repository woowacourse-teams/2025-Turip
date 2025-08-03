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
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.model.RegionModel
import com.on.turip.ui.main.RegionAdapter
import com.on.turip.ui.search.keywordresult.SearchResultActivity
import com.on.turip.ui.search.regionresult.RegionResultActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()

    private val metropolitanCitiesAdapter: RegionAdapter =
        RegionAdapter { region: RegionModel ->
            val intent = RegionResultActivity.newIntent(requireContext(), region.english)
            startActivity(intent)
        }
    private val provincesAdapter: RegionAdapter =
        RegionAdapter { region: RegionModel ->
            val intent = RegionResultActivity.newIntent(requireContext(), region.english)
            startActivity(intent)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        setupTextHighlighting()
        setupAdapters()
        setupObservers()
        setupBindings()

        return view
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    private fun setupBindings() {
        binding.ivHomeSearch.setOnClickListener {
            val intent = SearchResultActivity.newIntent(requireContext())
            startActivity(intent)
        }
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
                            requireActivity(),
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
        binding.rvHomeMetropolitanCity.adapter = metropolitanCitiesAdapter
        binding.rvHomeProvince.adapter = provincesAdapter
    }

    private fun setupObservers() {
        viewModel.metropolitanCities.observe(viewLifecycleOwner) { metropolitanCities: List<RegionModel> ->
            metropolitanCitiesAdapter.submitList(metropolitanCities)
        }

        viewModel.provinces.observe(viewLifecycleOwner) { provinces: List<RegionModel> ->
            provincesAdapter.submitList(provinces)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
