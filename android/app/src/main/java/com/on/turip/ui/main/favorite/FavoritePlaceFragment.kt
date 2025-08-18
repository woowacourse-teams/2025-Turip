package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.FragmentFavoritePlaceBinding
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.folder.FolderActivity
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFragment : BaseFragment<FragmentFavoritePlaceBinding>() {
    private val viewModel: FavoritePlaceViewModel by viewModels { FavoritePlaceViewModel.provideFactory() }
    private val folderAdapter: FavoritePlaceFolderAdapter by lazy {
        FavoritePlaceFolderAdapter { folderId: Long ->
            viewModel.updateFolderWithPlaces(folderId)
        }
    }
    private val placeAdapter: FavoritePlaceAdapter by lazy {
        FavoritePlaceAdapter(
            object : FavoritePlaceViewHolder.FavoritePlaceListener {
                override fun onFavoriteClick(
                    placeId: Long,
                    isFavorite: Boolean,
                ) {
                    // TODO: 장소 찜 클릭 시 로직
                }

                override fun onMapClick(uri: Uri) {
                    // TODO: 지도 버튼 클릭 시 로직
                }
            },
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoritePlaceBinding = FragmentFavoritePlaceBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupAdapters() {
        binding.rvFavoritePlaceFolder.apply {
            adapter = folderAdapter
            itemAnimator = null
            addOnItemTouchListener(RecyclerViewTouchInterceptor)
        }
        binding.rvFavoritePlacePlace.adapter = placeAdapter
    }

    private fun setupListeners() {
        binding.ivFavoritePlaceFolder.setOnClickListener {
            val intent: Intent = FolderActivity.newIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.folders.observe(viewLifecycleOwner) { favoritePlaceFolders: List<FavoritePlaceFolderModel> ->
            folderAdapter.submitList(favoritePlaceFolders)
        }

        viewModel.placeCount.observe(viewLifecycleOwner) { placeCount: Int ->
            if (placeCount == 0) {
                binding.clFavoritePlaceEmpty.visibility = View.VISIBLE
                binding.groupFavoritePlaceNotEmpty.visibility = View.GONE
            } else {
                binding.clFavoritePlaceEmpty.visibility = View.GONE
                binding.groupFavoritePlaceNotEmpty.visibility = View.VISIBLE
                binding.tvFavoritePlacePlaceCount.text =
                    getString(R.string.all_total_place_count, placeCount)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFoldersAndPlaces()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.loadFoldersAndPlaces()
        }
    }

    companion object {
        fun instance(): FavoritePlaceFragment = FavoritePlaceFragment()
    }
}

private object RecyclerViewTouchInterceptor : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent,
    ): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }

    override fun onTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent,
    ) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
