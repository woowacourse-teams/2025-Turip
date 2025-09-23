package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderCatalogBinding
import com.on.turip.ui.common.base.BaseFragment

class FavoritePlaceFolderCatalogFragment : BaseFragment<BottomSheetFragmentFavoritePlaceFolderCatalogBinding>() {
    private val placeAdapter: FavoritePlaceAdapter by lazy {
        FavoritePlaceAdapter(
            object : FavoritePlaceViewHolder.FavoritePlaceListener {
                override fun onFavoriteClick(
                    placeId: Long,
                    isFavorite: Boolean,
                ) {
                }

                override fun onMapClick(uri: Uri) {
                    val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            },
            onChange = TODO(),
        )
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
