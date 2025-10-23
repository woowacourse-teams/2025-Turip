package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderCatalogBinding
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FavoritePlaceFolderCatalogFragment : BaseFragment<BottomSheetFragmentFavoritePlaceFolderCatalogBinding>() {
    @Inject
    lateinit var favoritePlaceFolderCatalogViewModelFactory: FavoritePlaceFolderCatalogViewModel.FolderInformationAssistedFactory

    private val viewModel: FavoritePlaceFolderCatalogViewModel by viewModels {
        FavoritePlaceFolderCatalogViewModel.provideFactory(
            favoritePlaceFolderCatalogViewModelFactory,
            folderId = arguments?.getLong(ARGUMENTS_FOLDER_ID) ?: 0L,
            folderName = arguments?.getString(ARGUMENTS_FOLDER_NAME) ?: "",
        )
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

                override fun onItemClick(favoritePlaceModel: FavoritePlaceModel) {
                    Timber.d("아이템 클릭: $favoritePlaceModel")
                }
            },
            onCommit = { viewModel.updateFavoritePlacesOrder(it) },
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
        setupListeners()
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoritePlaceFolderCatalog.adapter = placeAdapter

        binding.btnBottomSheetFolderFavoritePlaceFolderCatalogBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val itemTouchHelper =
            ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    0,
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder,
                    ): Boolean {
                        val from = viewHolder.bindingAdapterPosition
                        val to = target.bindingAdapterPosition
                        placeAdapter.moveItem(from, to)
                        return true
                    }

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int,
                    ) = Unit

                    override fun isLongPressDragEnabled(): Boolean = true

                    override fun interpolateOutOfBoundsScroll(
                        recyclerView: RecyclerView,
                        viewSize: Int,
                        viewSizeOutOfBounds: Int,
                        totalSize: Int,
                        msSinceStartScroll: Long,
                    ): Int = viewSizeOutOfBounds / 10

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        placeAdapter.commitMove()
                    }
                },
            )

        itemTouchHelper.attachToRecyclerView(binding.rvBottomSheetFavoritePlaceFolderCatalog)
    }

    private fun setupObservers() {
        viewModel.favoritePlaceUiState.observe(viewLifecycleOwner) { state ->
            placeAdapter.submitList(state.places)

            binding.tvBottomSheetFolderFavoritePlaceFolderCatalogTitle.text = state.folderName

            binding.tvBottomSheetFavoritePlaceFolderCount.text =
                getString(R.string.all_total_place_count, state.places.size)

            if (state.places == emptyList<FavoritePlaceModel>()) {
                binding.ivBottomSheetFavoritePlaceFolderShare.visibility = View.GONE
            } else {
                binding.ivBottomSheetFavoritePlaceFolderShare.visibility = View.VISIBLE
            }
        }

        viewModel.shareFolder.observe(viewLifecycleOwner) { shareFolder: FavoriteFolderShareModel ->
            makeShareIntent(shareFolder)
        }
    }

    private fun setupListeners() {
        binding.ivBottomSheetFavoritePlaceFolderShare.setOnClickListener {
            viewModel.shareFolder()
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

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFavoritePlaceFolderCatalogBinding =
        BottomSheetFragmentFavoritePlaceFolderCatalogBinding.inflate(inflater, container, false)

    companion object {
        private const val ARGUMENTS_FOLDER_ID = "FOLDER_ID"
        private const val ARGUMENTS_FOLDER_NAME = "FOLDER_NAME"
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun newInstance(
            folderId: Long,
            folderName: String,
        ): FavoritePlaceFolderCatalogFragment =
            FavoritePlaceFolderCatalogFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(ARGUMENTS_FOLDER_ID, folderId)
                        putString(ARGUMENTS_FOLDER_NAME, folderName)
                    }
            }
    }
}
