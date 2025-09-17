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
import com.on.turip.domain.ErrorEvent
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.folder.FolderActivity
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel

class FavoritePlaceFragment : BaseFragment<FragmentFavoritePlaceBinding>() {
    private val viewModel: FavoritePlaceViewModel by viewModels { FavoritePlaceViewModel.provideFactory() }
    private val folderNameAdapter: FavoritePlaceFolderNameAdapter by lazy {
        FavoritePlaceFolderNameAdapter { folderId: Long ->
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
                    viewModel.updateFavoritePlace(placeId, isFavorite)
                }

                override fun onMapClick(uri: Uri) {
                    val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
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
        showNetworkError()
    }

    private fun showNetworkError() {
        binding.customErrorView.apply {
            visibility = View.VISIBLE
            setupError(ErrorEvent.NETWORK_ERROR)
            setOnRetryClickListener {
                viewModel.loadFoldersAndPlaces()
            }
        }
    }

    private fun setupAdapters() {
        binding.rvFavoritePlaceFolderName.apply {
            adapter = folderNameAdapter
            itemAnimator = null
            addOnItemTouchListener(RecyclerViewTouchInterceptor)
        }

        binding.rvFavoritePlacePlace.apply {
            adapter = placeAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.ivFavoritePlaceFolder.setOnClickListener {
            val intent: Intent = FolderActivity.newIntent(requireContext())
            startActivity(intent)
        }
        binding.ivFavoritePlaceShare.setOnClickListener {
            viewModel.shareFolder()
        }
    }

    private fun setupObservers() {
        viewModel.folders.observe(viewLifecycleOwner) { favoritePlaceFolders: List<FavoritePlaceFolderModel> ->
            folderNameAdapter.submitList(favoritePlaceFolders)
        }

        viewModel.places.observe(viewLifecycleOwner) { places: List<FavoritePlaceModel> ->
            placeAdapter.submitList(places)

            if (places.isEmpty()) {
                binding.clFavoritePlaceEmpty.visibility = View.VISIBLE
                binding.groupFavoritePlaceNotEmpty.visibility = View.GONE
            } else {
                binding.clFavoritePlaceEmpty.visibility = View.GONE
                binding.groupFavoritePlaceNotEmpty.visibility = View.VISIBLE
                binding.tvFavoritePlacePlaceCount.text =
                    getString(R.string.all_total_place_count, places.size)
            }
        }

        viewModel.shareFolder.observe(viewLifecycleOwner) { shareFolder: FavoriteFolderShareModel ->
            makeShareIntent(shareFolder)
        }

        viewModel.networkError.observe(viewLifecycleOwner) { networkError ->
            handleErrorOrContentView(networkError || (viewModel.serverError.value == true))
        }

        viewModel.serverError.observe(viewLifecycleOwner) { serverError ->
            handleErrorOrContentView(serverError || (viewModel.networkError.value == true))
        }
    }

    private fun makeShareIntent(shareFolder: FavoriteFolderShareModel) {
        val sharedContents: String = shareFolder.toShareFormat()

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sharedContents)
                putExtra(Intent.EXTRA_TITLE, shareFolder.name)
            }
        val kakaoIntent: Intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                `package` = KAKAO_PACKAGE
                putExtra(Intent.EXTRA_TEXT, sharedContents)
            }
        val instagramIntent: Intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                `package` = INSTAGRAM_PACKAGE
                putExtra(Intent.EXTRA_TEXT, sharedContents)
            }
        val initialIntents = arrayOf(kakaoIntent, instagramIntent)

        val chooserIntent =
            Intent.createChooser(intent, shareFolder.name).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
                putExtra(Intent.EXTRA_TITLE, shareFolder.name)
            }

        startActivity(chooserIntent)
    }

    private fun handleErrorOrContentView(isError: Boolean) {
        if (isError) {
            binding.customErrorView.visibility = View.VISIBLE
            binding.groupFavoritePlaceNotEmpty.visibility = View.GONE
            binding.groupFavoritePlaceNotError.visibility = View.GONE
        } else {
            binding.customErrorView.visibility = View.GONE
            binding.groupFavoritePlaceNotEmpty.visibility = View.VISIBLE
            binding.groupFavoritePlaceNotError.visibility = View.VISIBLE
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
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

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
