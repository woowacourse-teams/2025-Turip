package com.on.turip.ui.login

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.on.turip.databinding.ActivityLoginBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.compose.login.LoginScreen
import com.on.turip.ui.compose.theme.TuripTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ComposeView(this).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                TuripTheme {
                    LoginScreen()
                }
            }
        }
    }
}
