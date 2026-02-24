package com.on.turip.ui.main.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.TuripPlaceScreen
import com.on.turip.ui.compose.myturip.MyTuripScreen
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.TuripShareModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TuripPlaceFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TuripTheme {
                    var selectedTuripId: Long by rememberSaveable { mutableLongStateOf(0L) }
                    var currentScreen: Int by rememberSaveable { mutableIntStateOf(0) }

                    when (currentScreen) {
                        0 -> {
                            MyTuripScreen(
                                onNavigateToTuripPlace = { newId ->
                                    selectedTuripId = newId
                                    currentScreen = 1
                                },
                                onNavigateToLogin = ::navigateToLoginScreen,
                            )
                        }

                        1 -> {
                            TuripPlaceScreen(
                                selectedTuripId = selectedTuripId,
                                onNavigateToLogin = ::navigateToLoginScreen,
                                onShareTurip = ::navigateToShareTurip,
                                onNavigateToMap = {},
                                goBack = {
                                    currentScreen = 0
                                },
                            )
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

    private fun navigateToShareTurip(turipShareModel: TuripShareModel) {
        val sharedContents: String = turipShareModel.toShareFormat()

        val intent =
            createShareIntent(text = sharedContents).apply {
                putExtra(Intent.EXTRA_TITLE, turipShareModel.name)
            }

        val initialIntents =
            arrayOf(
                createShareIntent(text = sharedContents, packageName = KAKAO_PACKAGE),
                createShareIntent(text = sharedContents, packageName = INSTAGRAM_PACKAGE),
            )

        val chooserIntent =
            Intent
                .createChooser(intent, turipShareModel.name)
                .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents) }

        startActivity(chooserIntent)
    }

    private fun createShareIntent(
        text: String,
        packageName: String? = null,
    ): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            packageName?.let { `package` = it }
        }

    companion object {
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun instance(): TuripPlaceFragment = TuripPlaceFragment()
    }
}
