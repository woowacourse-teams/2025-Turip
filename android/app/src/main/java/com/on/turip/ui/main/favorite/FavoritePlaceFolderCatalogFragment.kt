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
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderCatalogBinding
import com.on.turip.ui.common.base.BaseFragment

class FavoritePlaceFolderCatalogFragment : BaseFragment<BottomSheetFragmentFavoritePlaceFolderCatalogBinding>() {
    private val viewModel: FavoritePlaceFolderCatalogViewModel by viewModels {
        FavoritePlaceFolderCatalogViewModel.provideFactory(
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
            },
            onChange = { viewModel.updateFavoritePlacesOrder(it) },
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
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
                },
            )

        itemTouchHelper.attachToRecyclerView(binding.rvBottomSheetFavoritePlaceFolderCatalog)
    }

    private fun setupObservers() {
        viewModel.favoritePlaceUiState.observe(viewLifecycleOwner) { state ->
            placeAdapter.submitList(state.places)

            binding.tvBottomSheetFolderFavoritePlaceFolderCatalogTitle.text = state.folderName
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFavoritePlaceFolderCatalogBinding =
        BottomSheetFragmentFavoritePlaceFolderCatalogBinding.inflate(inflater, container, false)

    companion object {
        private const val ARGUMENTS_FOLDER_ID = "FOLDER_ID"
        private const val ARGUMENTS_FOLDER_NAME = "FOLDER_NAME"

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
