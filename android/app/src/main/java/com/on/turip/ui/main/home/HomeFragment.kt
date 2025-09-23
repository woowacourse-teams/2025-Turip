package com.on.turip.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.on.turip.ui.compose.main.home.HomeScreen
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.main.home.model.UsersLikeContentModel
import com.on.turip.ui.search.keywordresult.SearchActivity
import com.on.turip.ui.search.regionresult.RegionResultActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                TuripTheme {
                    HomeScreen(
                        onSearchClick = { keyword: String ->
                            if (keyword.isNotBlank()) {
                                Timber.d("검색 버튼 클릭 $keyword")
                                val intent: Intent =
                                    SearchActivity.newIntent(requireContext(), keyword)
                                startActivity(intent)
                            }
                        },
                        onRegionClick = { regionCategoryName: String ->
                            Timber.d("지역 선택 : $regionCategoryName")
                            val intent: Intent =
                                RegionResultActivity.newIntent(requireContext(), regionCategoryName)
                            startActivity(intent)
                        },
                        onContentClick = { usersLikeContent: UsersLikeContentModel ->
                            Timber.d(
                                "인기 컨텐츠 선택 : ContentId = ${usersLikeContent.content.id} CreatorId = ${usersLikeContent.content.creator.id}",
                            )
                            val intent: Intent =
                                TripDetailActivity.newIntent(
                                    context = requireContext(),
                                    contentId = usersLikeContent.content.id,
                                    creatorId = usersLikeContent.content.creator.id,
                                )
                            startActivity(intent)
                        },
                    )
                }
            }
        }
}
