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
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderCatalogBinding
import com.on.turip.ui.common.TuripDialogFragment
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderCatalogUiEffect
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderCatalogUiState
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FavoritePlaceFolderCatalogFragment :
    BaseFragment<BottomSheetFragmentFavoritePlaceFolderCatalogBinding>() {
    private val viewModel: FavoritePlaceFolderCatalogViewModel by viewModels()

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
        setupLoginSuggestDialog()
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoritePlaceFolderCatalog.adapter = placeAdapter

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
        collectOnStarted(viewModel.uiState) { uiState: FavoritePlaceFolderCatalogUiState ->
            placeAdapter.submitList(uiState.places)
            binding.tvBottomSheetFolderFavoritePlaceFolderCatalogTitle.text = uiState.folderName

            binding.tvBottomSheetFavoritePlaceFolderCount.text =
                getString(R.string.all_total_place_count, uiState.places.size)

            if (uiState.places == emptyList<FavoritePlaceModel>()) {
                binding.ivBottomSheetFavoritePlaceFolderShare.visibility = View.GONE
            } else {
                binding.ivBottomSheetFavoritePlaceFolderShare.visibility = View.VISIBLE
            }
        }

        collectOnStarted(viewModel.uiEffect) { uiEffect: FavoritePlaceFolderCatalogUiEffect ->
            when (uiEffect) {
                FavoritePlaceFolderCatalogUiEffect.NavigateToLogin -> navigateToLoginScreen()

                FavoritePlaceFolderCatalogUiEffect.ShowFolderShareNotAllowed -> {
                    showSuggestLoginMessage()
                }

                is FavoritePlaceFolderCatalogUiEffect.ShareFolder -> {
                    shareFolder(uiEffect.favoriteFolderShareModel)
                }

                is FavoritePlaceFolderCatalogUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply {
                                setAction(uiModel.retryTextRes) {
                                    viewModel.onErrorRetryRequested(
                                        uiEffect.action,
                                    )
                                }
                            }.show()
                    }
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(requireActivity())
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showSuggestLoginMessage() {
        TuripDialogFragment
            .newInstance(
                title = getString(R.string.turip_dialog_login_suggest_title),
                description = getString(R.string.turip_dialog_login_suggest_description),
                confirmText = getString(R.string.turip_dialog_login_suggest_confirm),
                dismissText = getString(R.string.turip_dialog_login_suggest_dismiss),
            ).show(parentFragmentManager, TuripDialogFragment::class.java.simpleName)
    }

    private fun setupListeners() {
        binding.ivBottomSheetFavoritePlaceFolderShare.setOnClickListener {
            viewModel.shareFolder()
        }

        binding.btnBottomSheetFolderFavoritePlaceFolderCatalogBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun shareFolder(folderShareModel: FavoriteFolderShareModel) {
        val sharedContents: String = folderShareModel.toShareFormat()

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sharedContents)
                putExtra(Intent.EXTRA_TITLE, folderShareModel.name)
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
            Intent.createChooser(intent, folderShareModel.name).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
                putExtra(Intent.EXTRA_TITLE, folderShareModel.name)
            }

        startActivity(chooserIntent)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFavoritePlaceFolderCatalogBinding =
        BottomSheetFragmentFavoritePlaceFolderCatalogBinding.inflate(inflater, container, false)

    private fun setupLoginSuggestDialog() {
        parentFragmentManager.setFragmentResultListener(
            TuripDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, bundle ->

            when (bundle.getString(TuripDialogFragment.TURIP_DIALOG_RESULT)) {
                TuripDialogFragment.RESULT_CONFIRM -> {
                    navigateToLoginScreen()
                }
            }
        }
    }

    companion object {
        const val FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID =
            "com.on.turip.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID"
        const val FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME =
            "com.on.turip.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME"
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun newInstance(
            folderId: Long,
            folderName: String,
        ): FavoritePlaceFolderCatalogFragment =
            FavoritePlaceFolderCatalogFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID, folderId)
                        putString(FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME, folderName)
                    }
            }
    }
}
